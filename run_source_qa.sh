#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
python "$ROOT/qa_check_repository_layout.py"
ANDROID="$ROOT/URJA_KENDRA_Android_Server_Assessment_v1.5"
python "$ANDROID/qa/check_android_ci_workflow.py"
python "$ANDROID/qa/check_assessment_api_contract.py"
python "$ANDROID/qa/check_certificate_resume.py"
python "$ANDROID/qa/check_release_network_security.py"
(cd "$ROOT/URJA_KENDRA_Backend_Starter_v0.4" && python -m compileall -q app tests)
echo "PASS: source-level QA suite completed. This does not build the APK or run backend pytest."
