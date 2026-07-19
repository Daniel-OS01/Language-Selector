#!/usr/bin/env bash
# Resolve the next SemVer for a publishable APK release.
#
# Inputs (env):
#   BUMP              - minor (default) | major
#   GITHUB_SHA        - commit being released (required)
#   FALLBACK_VERSION  - last known version when no v* tags exist (default 2.0.0)
#   GITHUB_OUTPUT     - optional; when set, writes GitHub Actions outputs
#
# Prints: VERSION_NAME VERSION_CODE TAG RELEASE_TITLE REUSED
# and appends the same keys to GITHUB_OUTPUT when present.

set -euo pipefail

bump=${BUMP:-minor}
fallback=${FALLBACK_VERSION:-2.0.0}
sha=${GITHUB_SHA:?GITHUB_SHA must be set}
output_file=${GITHUB_OUTPUT:-}

semver_re='^[0-9]+\.[0-9]+\.[0-9]+$'
tag_re='^v[0-9]+\.[0-9]+\.[0-9]+$'

if [[ "$bump" != "minor" && "$bump" != "major" ]]; then
  printf 'BUMP must be minor or major, got: %s\n' "$bump" >&2
  exit 1
fi

if [[ ! "$fallback" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  printf 'FALLBACK_VERSION must be MAJOR.MINOR.PATCH, got: %s\n' "$fallback" >&2
  exit 1
fi

strip_v() {
  local value=$1
  printf '%s\n' "${value#v}"
}

is_semver_tag() {
  [[ "$1" =~ $tag_re ]]
}

version_code_from() {
  local version=$1
  local major minor patch
  IFS=. read -r major minor patch <<< "$version"
  printf '%s\n' "$((major * 1000000 + minor * 1000 + patch))"
}

list_semver_tags() {
  git tag -l 'v*' 2>/dev/null | while IFS= read -r tag; do
    if is_semver_tag "$tag"; then
      printf '%s\n' "$tag"
    fi
  done
}

latest_semver_tag() {
  list_semver_tags | sort -V | tail -n 1
}

semver_tag_for_sha() {
  git tag --points-at "$sha" 2>/dev/null | while IFS= read -r tag; do
    if is_semver_tag "$tag"; then
      printf '%s\n' "$tag"
    fi
  done | sort -V | tail -n 1
}

bump_version() {
  local current=$1
  local mode=$2
  local major minor patch
  IFS=. read -r major minor patch <<< "$current"
  case "$mode" in
    major)
      major=$((major + 1))
      minor=0
      patch=0
      ;;
    minor)
      minor=$((minor + 1))
      patch=0
      ;;
  esac
  printf '%s.%s.%s\n' "$major" "$minor" "$patch"
}

reused=false
existing_tag=$(semver_tag_for_sha || true)
if [[ -n "${existing_tag:-}" ]]; then
  version_name=$(strip_v "$existing_tag")
  reused=true
else
  latest_tag=$(latest_semver_tag || true)
  if [[ -n "${latest_tag:-}" ]]; then
    base=$(strip_v "$latest_tag")
  else
    base=$fallback
  fi
  if [[ ! "$base" =~ $semver_re ]]; then
    printf 'Resolved base version is not SemVer: %s\n' "$base" >&2
    exit 1
  fi
  version_name=$(bump_version "$base" "$bump")
fi

version_code=$(version_code_from "$version_name")
tag="v${version_name}"
release_title="$tag"

emit() {
  local key=$1
  local value=$2
  printf '%s=%s\n' "$key" "$value"
  if [[ -n "$output_file" ]]; then
    printf '%s=%s\n' "$key" "$value" >> "$output_file"
  fi
}

emit version_name "$version_name"
emit version_code "$version_code"
emit tag "$tag"
emit release_title "$release_title"
emit reused "$reused"
emit short_sha "${sha:0:7}"
