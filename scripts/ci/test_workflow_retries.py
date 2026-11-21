# ruff: noqa: S603, S607
"""Exercise workflow shell against isolated Git repositories and fake GitHub APIs."""
from __future__ import annotations

import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]


def workflow_run(filename: str, name: str) -> str:
    text = (ROOT / '.github/workflows' / filename).read_text()
    step = text.split(f'- name: {name}\n', 1)[1].split('\n      - ', 1)[0]
    body = step.split('        run: |\n', 1)[1]
    return '\n'.join(line[10:] if line.startswith('          ') else line for line in body.splitlines())


class WorkflowRetriesTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.bin = self.root / 'bin'
        self.bin.mkdir()
        self.env = os.environ.copy()
        self.env.update(HOME=str(self.root), GIT_CONFIG_NOSYSTEM='1',
                        PATH=f'{self.bin}:{os.environ["PATH"]}',
                        GITHUB_REPOSITORY='example/project', GITHUB_OUTPUT=str(self.root / 'output'),
                        GITHUB_REF='refs/heads/main', GITHUB_EVENT_NAME='push', BUMP='patch')

    def run_command(self, args, cwd=None, check=True):
        return subprocess.run(args, cwd=cwd or self.root, env=self.env, capture_output=True,
                              text=True, check=check)

    def fake_gh(self, code):
        path = self.bin / 'gh'
        path.write_text('#!/usr/bin/env python3\n' + code)
        path.chmod(0o755)

    def test_prepare_syncs_before_version_and_reuses_tag_on_rerun(self):
        origin = self.root / 'origin'
        self.run_command(['git', 'init', '-b', 'main', str(origin)])
        self.run_command(['git', 'config', 'user.email', 'ci@example.test'], origin)
        self.run_command(['git', 'config', 'user.name', 'CI'], origin)
        shutil.copytree(ROOT / 'scripts/ci', origin / 'scripts/ci', ignore=shutil.ignore_patterns('__pycache__'))
        self.run_command(['git', 'add', 'scripts'], origin)
        self.run_command(['git', 'commit', '-m', 'initial'], origin)
        self.run_command(['git', 'tag', 'v2.1.0'], origin)
        clone = self.root / 'clone'
        self.run_command(['git', 'clone', str(origin), str(clone)])
        old_sha = self.run_command(['git', 'rev-parse', 'HEAD'], clone).stdout.strip()
        self.env['GITHUB_SHA'] = old_sha
        self.run_command(['git', 'commit', '--allow-empty', '-m', 'dependency update\n\nCo-authored-by: dependabot[bot] <bot@example.test>'], origin)
        new_sha = self.run_command(['git', 'rev-parse', 'HEAD'], origin).stdout.strip()
        self.run_command(['bash', 'scripts/ci/prepare_release.sh'], clone)
        output = Path(self.env['GITHUB_OUTPUT']).read_text()
        self.assertIn(f'target_sha={new_sha}', output)
        self.assertIn('version_name=2.1.1', output)
        self.assertIn(f'short_sha={new_sha[:7]}', output)
        self.assertNotEqual(old_sha, new_sha)
        self.run_command(['git', 'tag', 'v2.1.1'], origin)
        Path(self.env['GITHUB_OUTPUT']).unlink()
        self.run_command(['bash', 'scripts/ci/prepare_release.sh'], clone)
        output = Path(self.env['GITHUB_OUTPUT']).read_text()
        self.assertIn('version_name=2.1.1', output)
        self.assertIn('reused=true', output)
        # Draft tags do not suppress a scheduled retry: only published releases count.
        self.env['GITHUB_EVENT_NAME'] = 'schedule'
        self.fake_gh("print(" + repr(json.dumps([[dict(tag_name="v2.1.0", draft=False, prerelease=False)]])) + ")\n")
        Path(self.env['GITHUB_OUTPUT']).unlink()
        self.run_command(['bash', 'scripts/ci/prepare_release.sh'], clone)
        self.assertIn('should_build=true', Path(self.env['GITHUB_OUTPUT']).read_text())
        self.fake_gh("print(" + repr(json.dumps([[dict(tag_name="v2.1.1", draft=False, prerelease=False)]])) + ")\n")
        Path(self.env['GITHUB_OUTPUT']).unlink()
        self.run_command(['bash', 'scripts/ci/prepare_release.sh'], clone)
        self.assertIn('should_build=false', Path(self.env['GITHUB_OUTPUT']).read_text())
        self.assertEqual(new_sha, self.run_command(['git', 'rev-parse', 'HEAD'], origin).stdout.strip())

    def test_prepare_survives_checkout_of_different_script_version(self):
        origin = self.root / 'origin'
        self.run_command(['git', 'init', '-b', 'main', str(origin)])
        self.run_command(['git', 'config', 'user.email', 'ci@example.test'], origin)
        self.run_command(['git', 'config', 'user.name', 'CI'], origin)
        shutil.copytree(ROOT / 'scripts/ci', origin / 'scripts/ci', ignore=shutil.ignore_patterns('__pycache__'))
        # Exceed Bash's read buffer; tiny scripts can accidentally mask the bug.
        running_script = origin / 'scripts/ci/prepare_release.sh'
        running_script.write_text(running_script.read_text().replace(
            "echo 'should_build=true'", ('# padding for shell read boundary\n' * 1000) + "echo 'should_build=true'"))
        self.run_command(['git', 'add', 'scripts'], origin)
        self.run_command(['git', 'commit', '-m', 'initial'], origin)
        clone = self.root / 'clone'
        self.run_command(['git', 'clone', str(origin), str(clone)])
        # Checkout replaces the very file Bash is reading with a shorter version.
        (origin / 'scripts/ci/prepare_release.sh').write_text('#!/usr/bin/env bash\nexit 97\n')
        self.run_command(['git', 'add', 'scripts'], origin)
        self.run_command(['git', 'commit', '-m', 'replace prepare script'], origin)
        sha = self.run_command(['git', 'rev-parse', 'HEAD'], origin).stdout.strip()
        result = self.run_command(['bash', 'scripts/ci/prepare_release.sh'], clone, check=False)
        self.assertEqual(0, result.returncode, result.stderr)
        output = Path(self.env['GITHUB_OUTPUT']).read_text()
        self.assertIn(f'target_sha={sha}', output)
        self.assertIn('version_name=2.0.1', output)

    def test_weekly_scan_uses_only_published_canonical_semver_across_pages(self):
        repo = self.root / 'repo'
        self.run_command(['git', 'init', '-b', 'main', str(repo)])
        self.run_command(['git', 'config', 'user.email', 'ci@example.test'], repo)
        self.run_command(['git', 'config', 'user.name', 'CI'], repo)
        shutil.copytree(ROOT / 'scripts/ci', repo / 'scripts/ci', ignore=shutil.ignore_patterns('__pycache__'))
        self.run_command(['git', 'add', 'scripts'], repo)
        self.run_command(['git', 'commit', '-m', 'initial'], repo)
        self.run_command(['git', 'remote', 'add', 'origin', str(repo)], repo)
        self.run_command(['git', 'tag', 'v2.9.0'], repo)
        self.run_command(['git', 'commit', '--allow-empty', '-m',
                          'update\n\nSigned-off-by: dependabot[bot] <bot@example.test>'], repo)
        self.run_command(['git', 'tag', 'v2.10.0'], repo)
        self.env.update(GITHUB_EVENT_NAME='schedule', GITHUB_REF='refs/heads/fixture')
        published = lambda tag: dict(tag_name=tag, draft=False, prerelease=False)
        cases = (
            ([[published('sha-historical'), dict(published('v99.0.0'), draft=True),
               dict(published('v88.0.0'), prerelease=True), published('v02.0.0')],
              [published('v2.9.0')]], 'true'),
            ([[published('sha-historical'), published('v2.10.0')],
              [published('v2.9.0')]], 'false'),
            ([[published('sha-historical'), dict(published('v99.0.0'), draft=True)]], 'true'),
        )
        output = Path(self.env['GITHUB_OUTPUT'])
        for pages, expected in cases:
            with self.subTest(pages=pages):
                self.fake_gh('import sys\nassert "--paginate" in sys.argv and "--slurp" in sys.argv\nprint(' + repr(json.dumps(pages)) + ')\n')
                output = Path(self.env['GITHUB_OUTPUT'])
                output.unlink(missing_ok=True)
                result = self.run_command(['bash', str(ROOT / 'scripts/ci/prepare_release.sh')], repo, check=False)
                self.assertEqual(0, result.returncode, result.stderr)
                self.assertIn(f'should_build={expected}', output.read_text())
        self.fake_gh('import sys\nsys.exit(1)\n')
        output.unlink(missing_ok=True)
        result = self.run_command(['bash', str(ROOT / 'scripts/ci/prepare_release.sh')], repo, check=False)
        self.assertNotEqual(0, result.returncode)
        self.assertNotIn('should_build=true', output.read_text())

    def test_prepare_parses_checkout_in_a_single_main_function(self):
        source = (ROOT / 'scripts/ci/prepare_release.sh').read_text()
        self.assertIn('main() {', source)
        self.assertGreater(source.index('git checkout'), source.index('main() {'))
        self.assertTrue(source.rstrip().endswith('main "$@"'))

    def setup_publication(self, initial=None):
        self.env.update(APK_NAME='app.apk', RELEASE_TAG='v2.1.1', RELEASE_TITLE='v2.1.1',
                        TARGET_SHA='a' * 40, RELEASE_NOTES='notes', STATE=str(self.root / 'state'))
        (self.root / 'dist').mkdir()
        (self.root / 'dist/app.apk').write_bytes(b'test-only-apk')
        if initial is not None:
            initial.setdefault('targetCommitish', self.env['TARGET_SHA'])
            Path(self.env['STATE']).write_text(json.dumps(initial))
        self.fake_gh('''import json, os, pathlib, sys
p = pathlib.Path(os.environ['STATE'])
a = sys.argv[1:]
s = json.loads(p.read_text()) if p.exists() else None
with open(str(p) + '.calls', 'a') as log: log.write(' '.join(a) + '\\n')
if a[0] == 'api':
    if 'matching-refs' in a[1]:
        print('[]' if not os.environ.get('TAG_SHA') else json.dumps([{'ref': 'refs/tags/' + os.environ['RELEASE_TAG']}]))
    else:
        print(os.environ['TAG_SHA'])
elif a[:2] == ['release', 'view']:
    if s is None: sys.exit(1)
    print(json.dumps(s))
elif a[:2] == ['release', 'create']:
    s = {'isDraft': True, 'assets': [], 'targetCommitish': a[a.index('--target') + 1]}
    p.write_text(json.dumps(s))
    if os.environ.get('FAIL_UPLOAD'): sys.exit(1)
    s['assets'] = [{'name': 'app.apk'}]
elif a[:2] == ['release', 'upload']:
    if os.environ.get('FAIL_UPLOAD'): sys.exit(1)
    s['assets'] = [x for x in s['assets'] if x['name'] != 'app.apk'] + [{'name': 'app.apk'}]
elif a[:2] == ['release', 'edit']:
    assert s['assets'], 'must upload before publishing'
    s['isDraft'] = False
else: raise AssertionError(a)
if s is not None: p.write_text(json.dumps(s))
''')

    def publish(self):
        return self.run_command(['bash', '-euo', 'pipefail', '-c', workflow_run('publish-apk.yml', 'Publish idempotent SemVer release')], check=False)

    def test_failed_initial_upload_stays_draft_then_retry_publishes(self):
        self.setup_publication()
        self.env['FAIL_UPLOAD'] = '1'
        self.assertNotEqual(0, self.publish().returncode)
        self.assertTrue(json.loads(Path(self.env['STATE']).read_text())['isDraft'])
        self.assertNotIn('release edit', Path(self.env['STATE'] + '.calls').read_text())
        del self.env['FAIL_UPLOAD']
        result = self.publish()
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertFalse(json.loads(Path(self.env['STATE']).read_text())['isDraft'])

    def test_failed_existing_draft_upload_never_publishes(self):
        self.setup_publication({'isDraft': True, 'assets': []})
        self.env['FAIL_UPLOAD'] = '1'
        self.assertNotEqual(0, self.publish().returncode)
        self.assertNotIn('release edit', Path(self.env['STATE'] + '.calls').read_text())

    def test_stale_draft_target_is_not_uploaded_or_published(self):
        self.setup_publication({'isDraft': True, 'targetCommitish': 'b' * 40,
                                'assets': [{'name': 'old.apk'}]})
        result = self.publish()
        self.assertNotEqual(0, result.returncode, result.stdout)
        calls = Path(self.env['STATE'] + '.calls').read_text()
        self.assertNotIn('release upload', calls)
        self.assertNotIn('release edit', calls)

    def test_draft_with_extra_asset_is_not_published(self):
        self.setup_publication({'isDraft': True, 'assets': [{'name': 'old.apk'}]})
        result = self.publish()
        self.assertNotEqual(0, result.returncode, result.stdout)
        self.assertNotIn('release edit', Path(self.env['STATE'] + '.calls').read_text())

    def test_existing_tag_must_resolve_to_build_commit(self):
        self.setup_publication()
        self.env['TAG_SHA'] = 'b' * 40
        result = self.publish()
        self.assertNotEqual(0, result.returncode, result.stdout)
        self.assertNotIn('release create', Path(self.env['STATE'] + '.calls').read_text())
        self.env['TAG_SHA'] = self.env['TARGET_SHA']
        result = self.publish()
        self.assertEqual(0, result.returncode, result.stderr)

    def test_published_release_is_immutable_on_rerun(self):
        for assets, expected in (([{'name': 'app.apk'}], 0), ([], 1)):
            with self.subTest(assets=assets):
                # Reuse the same fixture path without re-creating dist.
                if not (self.root / 'dist').exists():
                    self.setup_publication()
                Path(self.env['STATE']).write_text(json.dumps({'isDraft': False, 'assets': assets}))
                result = self.publish()
                self.assertEqual(expected, result.returncode, result.stderr)
        calls = Path(self.env['STATE'] + '.calls').read_text()
        self.assertNotIn('release upload', calls)
        self.assertNotIn('release edit', calls)
        self.assertNotIn('release create', calls)

    def test_dependabot_success_and_review_events_retry_without_checkout(self):
        text = (ROOT / '.github/workflows/dependabot-auto-merge.yml').read_text()
        self.assertIn('check_suite:', text)
        self.assertIn('pull_request_review:', text)
        self.assertIn('workflow_run:', text)
        self.assertIn("github.event.check_suite.conclusion == 'success'", text)
        self.assertIn("github.event.workflow_run.conclusion == 'success'", text)
        self.assertNotIn('actions/checkout', text)
        self.assertIn('checks: read', text)
        self.assertIn('actions: read', text)

    def run_merge_gate(self, checks, *, run=None, fail_api='', candidates=None):
        sha = 'a' * 40
        self.env.update(PR_NUMBER='', GATE_STATE=str(self.root / 'gate.json'))
        payload = dict(checks=checks, run=run or dict(
            head_sha=sha, status='completed', conclusion='success',
            path='.github/workflows/release-build.yml',
            repository=dict(full_name='example/project'), event='pull_request'),
            fail_api=fail_api, candidates=candidates or {'42': sha})
        Path(self.env['GATE_STATE']).write_text(json.dumps(payload))
        self.fake_gh('''import json, os, pathlib, sys
p = pathlib.Path(os.environ['GATE_STATE'])
s = json.loads(p.read_text())
a = sys.argv[1:]
endpoint = next(x for x in a if x.startswith('repos/'))
with open(str(p) + '.calls', 'a') as log: log.write(' '.join(a) + '\\n')
if s['fail_api'] and s['fail_api'] in endpoint: sys.exit(1)
if '--method' in a:
    with open(str(p) + '.merges', 'a') as log: log.write(' '.join(a) + '\\n')
    print('merged')
elif '/check-runs?' in endpoint:
    assert '--paginate' in a and '--slurp' in a
    print(json.dumps([dict(check_runs=s['checks'])]))
elif '/actions/runs/' in endpoint: print(json.dumps(s['run']))
elif '?' in endpoint: print('\\n'.join(s['candidates']))
else: print(s['candidates'].get(endpoint.rsplit('/', 1)[1], ''))
''')
        result = self.run_command(['bash', '-euo', 'pipefail', '-c', workflow_run(
            'dependabot-auto-merge.yml', 'Merge Dependabot PRs')], check=False)
        merges = Path(self.env['GATE_STATE'] + '.merges')
        merged = merges.read_text() if merges.exists() else ''
        if merges.exists():
            merges.unlink()
        return result, merged

    def validation_check(self, **changes):
        check = dict(name='Test and build release APK', head_sha='a' * 40,
                     status='completed', conclusion='success', app=dict(slug='github-actions'),
                     details_url='https://github.com/example/project/actions/runs/123/job/456')
        check.update(changes)
        return check

    def test_dependabot_requires_completed_success_for_exact_head(self):
        for checks in ([], [self.validation_check(status='in_progress', conclusion=None)],
                       [self.validation_check(conclusion='failure')],
                       [self.validation_check(head_sha='b' * 40)],
                       [self.validation_check(name='Unrelated check')],
                       [self.validation_check(app=dict(slug='untrusted-app'))]):
            with self.subTest(checks=checks):
                result, merged = self.run_merge_gate(checks)
                self.assertEqual(0, result.returncode, result.stderr)
                self.assertEqual('', merged)

    def test_dependabot_checks_trusted_workflow_for_each_candidate(self):
        result, merged = self.run_merge_gate([self.validation_check()],
                                            candidates={'42': 'a' * 40, '43': 'b' * 40})
        self.assertEqual(0, result.returncode, result.stderr)
        self.assertIn('/pulls/42/merge', merged)
        self.assertIn('sha=' + 'a' * 40, merged)
        self.assertNotIn('/pulls/43/merge', merged)
        for changes in (dict(head_sha='b' * 40), dict(path='.github/workflows/unrelated.yml'),
                        dict(status='in_progress'), dict(conclusion='failure'),
                        dict(repository=dict(full_name='attacker/project'))):
            with self.subTest(changes=changes):
                run = dict(head_sha='a' * 40, status='completed', conclusion='success',
                           path='.github/workflows/release-build.yml',
                           repository=dict(full_name='example/project'), event='pull_request')
                run.update(changes)
                result, merged = self.run_merge_gate([self.validation_check()], run=run)
                self.assertEqual(0, result.returncode, result.stderr)
                self.assertEqual('', merged)

    def test_dependabot_api_errors_fail_closed(self):
        for endpoint in ('/pulls?', '/pulls/42', '/check-runs?', '/actions/runs/'):
            with self.subTest(endpoint=endpoint):
                result, merged = self.run_merge_gate([self.validation_check()], fail_api=endpoint)
                self.assertNotEqual(0, result.returncode)
                self.assertEqual('', merged)


if __name__ == '__main__':
    unittest.main()
