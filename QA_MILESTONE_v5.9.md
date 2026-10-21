# QA milestone v5.9 — clean handoff package

## Changes
- Removed generated Python bytecode and pytest cache directories from the handoff ZIP.
- Corrected the package and setup documentation to consistently name v5.9.
- Made the distinction between source-contract checks and real builds/tests explicit.
- Preserved Android and backend source, tests, CI workflows, and QA scripts.

## Checks executed for this milestone
- `bash run_source_qa.sh`: PASS (repository layout and source-contract checks).
- `python -m compileall -q app tests`: PASS (backend syntax compilation only).
- ZIP archive integrity: checked after packaging.

## Not verified
- Full backend `pytest` run: previously blocked in the local environment because `python-jose` was not installed.
- Android Gradle build, unit tests, lint, emulator/device install: not run here.
- GitHub Actions: not run because no repository connection was provided.

## Next release gate
Upload the repository-root contents to GitHub and run both Actions workflows. Fix actual CI failures before claiming a working APK. Then test OTP login, course enrolment/progress, assessment submission, certificate eligibility/download, and Hindi/English behavior on an emulator or physical Android device.
