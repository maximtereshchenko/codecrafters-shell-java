#!/bin/sh
#
# This script is used to run your program on CodeCrafters
#
# This runs after .codecrafters/compile.sh
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit on failure

stty -icanon -echo
exec java -jar /tmp/codecrafters-build-shell-java/codecrafters-shell.jar "$@"
stty sane