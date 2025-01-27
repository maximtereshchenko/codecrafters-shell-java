#!/usr/bin/env bash

set -Eeuo pipefail

./.codecrafters/compile.sh
./.codecrafters/run.sh
