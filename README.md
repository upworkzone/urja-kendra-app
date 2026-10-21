# URJA KENDRA Developer Handoff v6.1

This package contains the Android project, FastAPI backend, GitHub Actions workflows, and QA notes.

## Repository layout

The ZIP contains a wrapper folder named `URJA_KENDRA_Developer_Handoff_v6.1`. Copy the **contents inside that folder** into the GitHub repository root, including the hidden `.github` directory. The repository root must directly contain:

- `.github/workflows/android-build.yml`
- `.github/workflows/backend-tests.yml`
- `URJA_KENDRA_Android_Server_Assessment_v1.5/`
- `URJA_KENDRA_Backend_Starter_v0.4/`

GitHub only discovers workflow files under `.github/workflows` at repository root. Do not commit the wrapper as a nested folder.

## Local source checks

From the extracted wrapper folder:

```bash
bash run_source_qa.sh
```

This runs repository-layout and source-contract checks. It does **not** compile Android, run the full backend pytest suite, or install the app on a device.

## CI build and tests

1. Create a GitHub repository.
2. Copy the contents of this folder into the repository root, including `.github`.
3. Commit and push.
4. Open **Actions** and inspect **Backend tests** and **Android debug build**.
5. Download `urja-kendra-debug-apk` only if the Android workflow succeeds.
6. If a workflow fails, download its diagnostics artifact and address the first failing error before proceeding.

## Release status

No verified APK is included. The full backend pytest suite and Android Gradle build have not been verified in this workspace. Treat the project as a development starter until CI and device tests pass.

Do not use the development OTP stub, development secrets, or SQLite configuration in production. Configure a real OTP provider, HTTPS backend, production secrets, database migrations, monitoring, and abuse-resistant rate limiting before launch. Have qualified reviewers validate course safety and technical content, and approve certificate wording and issuer before public release.

## v6.1 backend fix

OTP login now preserves the language (`en` or `hi`) chosen when the OTP was requested and applies it to the user profile after successful verification. See `QA_MILESTONE_v6.1.md`. Existing databases need a reviewed migration to add the `locale` column; `create_all()` does not migrate an existing table. Backend pytest remains unverified until dependencies are installed, and no APK build is claimed.


## v6.1 database upgrade
For existing databases, back up first and follow `URJA_KENDRA_Backend_Starter_v0.4/migrations/README.md`. The migration adds `otp_challenges.locale` with an English fallback. Run backend regression tests after installing all requirements; the full test suite has not yet been verified in this environment.


## v6.2 milestone

See `QA_MILESTONE_v6.2.md` for executable OTP locale migration regression tests and commands to run them.

## v6.3 migration check
From `URJA_KENDRA_Backend_Starter_v0.4`, run `python qa/test_otp_locale_migration_standalone.py` to execute the SQLite OTP locale migration checks without importing the FastAPI app. This is a targeted check, not a replacement for the full pytest suite.


## v6.5 OTP security regression tests
The backend suite now includes tests for OTP challenge replay rejection and phone-number binding. Run the full backend test suite in an environment that can install `requirements.txt`; see `QA_MILESTONE_v6.5.md` for verification status.


## v6.5 milestone
See `QA_MILESTONE_v6.5.md` for the OTP failed-attempt regression test and verification status.


## v6.6 security note
OTP hash comparison now uses `hmac.compare_digest`. See `QA_MILESTONE_v6.6.md` for verification limits and test commands.

### v6.7 backend test runner
The Bash and PowerShell test runners report missing Python modules individually. From the backend directory, install dependencies with `python -m pip install -r requirements.txt`, then run the appropriate `qa/run_backend_tests` script. In offline environments, use an approved local wheelhouse. This diagnostic improvement does not imply that the complete test suite has passed.

## v6.8 source QA workflow
A dedicated `Source-level QA` GitHub Actions workflow now runs the dependency-free source checks on pushes and pull requests. This is separate from the backend test and Android build workflows; passing source QA does not mean those full builds/tests have passed. Run locally with `bash run_source_qa.sh`.
