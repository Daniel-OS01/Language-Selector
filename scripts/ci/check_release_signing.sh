#!/usr/bin/env bash

set -euo pipefail

publish_explicit=${PUBLISH_EXPLICIT:-false}
github_ref=${GITHUB_REF:-}
output_file=${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}
summary_file=${GITHUB_STEP_SUMMARY:-}

if [[ "$publish_explicit" != "true" && "$publish_explicit" != "false" ]]; then
  printf 'PUBLISH_EXPLICIT must be true or false, got: %s\n' "$publish_explicit" >&2
  exit 1
fi

if [[ "$github_ref" != "refs/heads/main" ]]; then
  printf 'Manual publication is restricted to refs/heads/main; got: %s\n' "$github_ref" >&2
  exit 1
fi

missing=()
[[ -n "${RELEASE_KEYSTORE_BASE64:-}" ]] || missing+=(RELEASE_KEYSTORE_BASE64)
[[ -n "${RELEASE_KEYSTORE_PASSWORD:-}" ]] || missing+=(RELEASE_KEYSTORE_PASSWORD)
[[ -n "${RELEASE_KEY_ALIAS:-}" ]] || missing+=(RELEASE_KEY_ALIAS)
[[ -n "${RELEASE_KEY_PASSWORD:-}" ]] || missing+=(RELEASE_KEY_PASSWORD)

if (( ${#missing[@]} > 0 )); then
  message="Missing GitHub Actions secrets: ${missing[*]}"

  if [[ "$publish_explicit" == "true" ]]; then
    printf '%s\n' "$message" >&2
    exit 1
  fi

  printf 'should_publish=false\n' >> "$output_file"
  printf '::warning title=Release publication skipped::%s. Continuing as an unsigned validation build.\n' "$message"

  if [[ -n "$summary_file" ]]; then
    {
      printf '### Release publication skipped\n\n'
      printf '%s. This automatic main run will continue as an unsigned validation build and will not create a GitHub Release.\n' "$message"
    } >> "$summary_file"
  fi

  exit 0
fi

printf 'should_publish=true\n' >> "$output_file"
