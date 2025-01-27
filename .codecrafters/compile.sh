#!/usr/bin/env bash

set -Eeuo pipefail

mvn -B -Dmaven.test.skip=true package
