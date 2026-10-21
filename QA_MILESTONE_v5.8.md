# URJA KENDRA — QA Milestone v5.8

## Purpose
This milestone consolidates repeatable source-level checks so the project can be reviewed before running the real Android build and backend test suite in CI.

## Verified in this workspace
- Android CI workflow contract checker: **PASS (9 assertions)**.
- Assessment API contract checker: **PASS** (Android/backend localized question and option fields).
- Certificate resume source checker: **PASS (6 assertions)**.
- Release network security source checker: **PASS (8 assertions)**.
- Python backend source compilation via `python -m compileall -q app tests`: **PASS**.
- Repository layout checker: **PASS** (Android workflow, backend workflow, Android project, backend project found at expected root-relative paths).
- ZIP archive integrity: **PASS**.

## Not verified — do not treat as release-ready
- Android APK compilation, unit tests, lint, installation, or device/emulator behavior.
- Backend runtime pytest suite. In the local workspace, `python -m pytest -q` stopped during import because the environment lacked `jose` (`python-jose`); tests did **not** pass or complete.
- Real SMS OTP delivery, production secrets, HTTPS deployment, database migration/backup, certificate policy, course technical accuracy, or Play Store compliance.

## Re-run local source checks
From the extracted handoff folder:

```bash
python qa_check_repository_layout.py
cd URJA_KENDRA_Android_Server_Assessment_v1.5
python qa/check_android_ci_workflow.py
python qa/check_assessment_api_contract.py
python qa/check_certificate_resume.py
python qa/check_release_network_security.py
cd ../URJA_KENDRA_Backend_Starter_v0.4
python -m compileall -q app tests
python -m pip install -r requirements.txt
python -m pytest -q
```

The final two commands require the project's dependencies to be installable. Run the Android build and backend tests using the included GitHub Actions workflows before distributing an APK. These source-level checks are not a substitute for a successful build/test run.

## Repository layout reminder
Copy the *contents inside* this handoff folder into the GitHub repository root, including `.github/`. Do not commit the handoff wrapper as a nested folder; GitHub Actions only discovers workflows at repository-root `.github/workflows/`.
