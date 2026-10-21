# URJA KENDRA Developer Handoff v4.3

## Milestone
Added a separate GitHub Actions workflow for backend dependency installation, Python compilation, and pytest execution. This complements the Android debug build workflow.

## Backend CI workflow
- File: `.github/workflows/backend-tests.yml`
- Uses Python 3.12 and the pinned `requirements.txt`.
- Uses development-only environment settings and a disposable SQLite database.
- Runs `compileall` and `pytest -q -ra`.
- Uploads the pytest log if the job fails.

## Important security note
The workflow's secret is explicitly CI/development-only. Never copy it into a production deployment. The OTP stub is permitted only for development/test runs.

## Verification performed locally
- Workflow YAML parsed with the available YAML parser (note: YAML 1.1 parsers may interpret the key `on` as a boolean; GitHub Actions uses its own workflow parser).
- Python syntax parsing for backend app/tests is available separately.
- Local pytest was attempted but could not collect tests because `python-jose` is not installed in this environment (`ModuleNotFoundError: No module named 'jose'`). Network access was not available to install pinned requirements.

## Not verified
- The GitHub Actions workflow has not been executed from this environment.
- Backend runtime tests and Android build are not yet confirmed passing.
- No installable APK has been generated here.

## Next steps
1. Push the extracted project to GitHub with the `.github/workflows` directory preserved.
2. Open Actions and run **Backend tests**.
3. If it fails, download `urja-kendra-backend-test-diagnostics` and share the first failing traceback.
4. Run **Android debug build** separately and resolve the first real compiler/build error.
