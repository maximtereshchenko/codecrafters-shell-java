#!/bin/sh
#
# Use this script to run your program LOCALLY.
#
# Note: Changing this script WILL NOT affect how CodeCrafters runs your program.
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit early if any commands fail
trap cleanup SIGINT SIGTERM ERR EXIT

cleanup() {
  stty sane
}

mvn -B package
stty -icanon -echo
java -jar ./target/codecrafters-shell.jar "$@"
