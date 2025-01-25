#!/bin/sh
#
# Use this script to run your program LOCALLY.
#
# Note: Changing this script WILL NOT affect how CodeCrafters runs your program.
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit early if any commands fail

(
  cd "$(dirname "$0")" # Ensure compile steps are run within the repository directory
  ./.codecrafters/compile.sh
)

path() {
  local path=""
  for command in "$@"; do
    path="$path:$(dirname $(which "$command"))"
  done
  echo "$path"
}

PATH="$(path mvn dirname stty)" ./.codecrafters/run.sh "$@"
