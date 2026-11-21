#!/usr/bin/env bash
# Main publication always builds the latest fetched main, never an old queued event.
# No source version commit or branch push is needed: Gradle consumes SemVer env.
set -euo pipefail

# Bash must parse the whole function before checkout can replace this source file.
main() {
  local output sha latest_tag releases update
  output=${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}
  if [[ "${GITHUB_REF:-}" == 'refs/heads/main' ]]; then
    git fetch origin main --tags
    git checkout --detach origin/main
  else
    git fetch origin --tags
  fi
  sha=$(git rev-parse HEAD)
  echo "target_sha=$sha" >> "$output"

  if [[ "${GITHUB_EVENT_NAME:-}" == 'schedule' ]]; then
    # Historical sha-* releases and unpublished drafts are not a SemVer baseline.
    # Read every page: old canonical releases may follow many historical releases.
    releases=$(gh api --paginate --slurp "repos/$GITHUB_REPOSITORY/releases?per_page=100")
    latest_tag=$(jq -r '
      [ .[][] | select(.draft == false and .prerelease == false)
        | .tag_name | select(test("^v(0|[1-9][0-9]{0,2})\\.(0|[1-9][0-9]{0,2})\\.(0|[1-9][0-9]{0,2})$")) ]
      | sort_by(ltrimstr("v") | split(".") | map(tonumber)) | last // ""' <<< "$releases")
    # A missing first canonical release bootstraps the schedule.
    if [[ -n "$latest_tag" ]]; then
      git merge-base --is-ancestor "$latest_tag" HEAD
      update=$(git log --format=%H --max-count=1 --fixed-strings \
        --grep='Co-authored-by: dependabot[bot] <' \
        --grep='Signed-off-by: dependabot[bot] <' "$latest_tag..HEAD")
      if [[ -z "$update" ]]; then
        echo 'should_build=false' >> "$output"
        return 0
      fi
    fi
  fi

  echo 'should_build=true' >> "$output"
  GITHUB_SHA=$sha bash scripts/ci/resolve_next_version.sh
}

main "$@"
