# URJA KENDRA Developer Handoff v6.8 — Source QA CI

## Change
- Added a dedicated GitHub Actions workflow for dependency-free source-level QA.
- The workflow runs on pushes, pull requests, and manual dispatch, separate from the full backend and Android build workflows.
- It runs repository layout, Android workflow/API/certificate/network-security source guards, and Python syntax compilation.

## Local verification
- `bash run_source_qa.sh`: run locally before pushing.
- Backend migration standalone tests: run separately using `python -m pytest -q qa/test_otp_locale_migration_standalone.py` from the backend directory.
- ZIP integrity: validated after packaging.

## Limits
- Source QA does not replace backend pytest with installed dependencies.
- Source QA does not compile the Android project or create an APK.
- The Android and backend CI workflows must still complete successfully before release readiness can be claimed.
