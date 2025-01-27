#!/usr/bin/env bash
set -euo pipefail
if [ -n "${DEBUG:-}" ]; then set -x ; env | sort ; fi

cd ${SENTINEL_BASE_DIR:=/sentinel}

export ENVIRONMENT="${DEPLOYMENT_ENVIRONMENT}"

java-tests \
  --module-name "hygieia-integration-tests" \
  --regression-test-pattern ".*IT\$" \
  --smoke-test-pattern ".*Smoke.*IT\$" \
  -Dsentinel="${DEPLOYMENT_ENVIRONMENT}" \
  $@

exit $?
