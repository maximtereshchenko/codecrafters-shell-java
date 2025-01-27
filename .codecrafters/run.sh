#!/usr/bin/env bash

set -Eeuo pipefail

stream_characters() {
  while IFS= read -r -s -n1 character; do
    if [[ "$character" = "" ]]; then
      printf $'\n'
    else
      printf "$character"
    fi
  done
}

java -jar ./target/codecrafters-shell.jar "$@" < <(stream_characters 2> /dev/null)