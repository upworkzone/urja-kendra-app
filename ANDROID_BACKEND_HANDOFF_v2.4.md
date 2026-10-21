# URJA KENDRA Developer Handoff v2.4

This handoff puts the current Android assessment-integration source and backend source/test harness in one package. It is intended for local developer setup and integration verification, not direct publication.

## Package contents

- `URJA_KENDRA_Android_Server_Assessment_v1.5/`: Android Kotlin/Compose source with server-assessment integration candidate.
- `URJA_KENDRA_Backend_Starter_v0.4/`: FastAPI backend, Dockerfile/Compose local setup, and QA scripts including the end-to-end smoke test.
- `ANDROID_BACKEND_HANDOFF_v2.4.md`: this guide.

## Suggested verification order

### 1. Backend

Requirements: Docker with Compose, or Python 3.11+ and pip.

With Docker:
```bash
cd URJA_KENDRA_Backend_Starter_v0.4
docker compose build
docker compose up -d
docker compose ps
```

Then run the smoke test in the same directory (requires Python locally and dependencies installed):
```bash
python -m pip install -r requirements.txt
python qa/run_e2e_smoke_test.py
```

Or without Docker:
```bash
cd URJA_KENDRA_Backend_Starter_v0.4
python -m venv .venv
# Activate .venv, then:
python -m pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```
In a second terminal, activate the same environment and run:
```bash
python qa/run_e2e_smoke_test.py
```

The smoke test writes test records and completes all eight EV-01 modules. Use only a disposable development database and the development OTP stub.

### 2. Android

Open `URJA_KENDRA_Android_Server_Assessment_v1.5/` in Android Studio. Let Gradle sync, then run the app on an emulator. For the Android emulator to reach a backend running on the host, the usual local base URL is `http://10.0.2.2:8000/`; verify the project's Retrofit base URL and Android cleartext-network configuration before testing.

This project has not been built in the current environment. If Android Studio reports a build error, fix that error and rerun before claiming an APK build passed.

### 3. Test sequence

1. OTP request and verification (development stub only).
2. Course catalogue in English and Hindi.
3. Enrol in EV-01.
4. Complete lessons and confirm progress persists.
5. Load and submit server-side assessment.
6. Confirm certificate eligibility response; no certificate is issued by this endpoint.

## Known limitations / release blockers

- No verified APK is included.
- Android Gradle build and live Android/backend integration have not been verified.
- Python test suite has previously been blocked because `python-jose` was missing; dependency installation also failed in the restricted environment due to DNS/network resolution. The smoke-test script is packaged but has not been run against a live API.
- OTP uses a development stub. Do not deploy it or expose its code response to real users.
- Replace the development signing secret, implement a real SMS OTP provider, rate limiting, HTTPS, deployment secret management, database migrations, backups, monitoring, and privacy/security review before production.
- Assessment covers EV-01 only. Other courses need reviewed lesson and assessment content.
- Certificate eligibility is not certificate issuance. Issuer identity, certificate template, verification and revocation policy, and final assessment rules remain to be defined.

## Branding reminder

Use the exact tagline: **“ऊर्जा बनाओ, ऊर्जा बचाओ, पैसे कमाओ...”**

## Release status

**Development handoff only — not release-ready.** Do not distribute to real learners until the build, API flow, security, and technical/safety content are reviewed and verified.


## Backend security startup fix (v2.5)
The bundled backend now validates release security settings at the beginning
of its FastAPI lifespan, before database initialization. This corrects a
mismatch between lifespan handling and the former legacy startup event.
See `URJA_KENDRA_Backend_Starter_v0.4/qa/SECURITY_STARTUP_FIX_v2.5.md`.
Source syntax check passed; full backend tests, Docker run, and Android build
remain unverified in this environment.


    ## Repeatable backend test runners (v2.6)
    Added `qa/run_backend_tests.sh` for macOS/Linux and `qa/run_backend_tests.ps1`
    for Windows PowerShell. They check key imports, compile Python files, and run
    pytest. See `qa/BACKEND_VERIFICATION_GUIDE_v2.6.md`. These scripts are supplied
    for execution on a machine with dependencies installed; tests have not been
    reported as passing merely because the scripts were created.
    

    ## Contract test correction (v2.7)
    Updated the learning contract test to avoid pinning the FastAPI application
    version to `0.3.0`, and added a source check for the release security validation
    order in the lifespan handler. Syntax parsing passed for app and test files;
    pytest itself remains unexecuted here.
    

    ## Android manifest build-blocker correction (v2.9)
    Replaced literal `\\n` text in AndroidManifest.xml with actual line breaks.
    All Android XML files now parse successfully. This is a source-level XML
    check, not a successful Gradle build. Production must switch from local HTTP
    emulator configuration to HTTPS and the deployed API URL.
    

    ## Android build check scripts (v3.0)
    Added Windows/macOS/Linux scripts and a build guide. No Gradle wrapper or
    system Gradle is available in this environment, so the Android build remains
    blocked pending Android Studio/JDK/SDK setup. No APK is claimed.
    

    ## Kotlin assessment DTO property correction (v3.1)
    Replaced invalid property references with existing bilingual helper functions
    in `ServerAssessmentScreen.kt`. Static source check passed; Kotlin compilation
    remains pending due to unavailable Gradle tooling in this environment.
    

    ## Certificate eligibility UI (v3.2)
    After a passing assessment, the app requests certificate eligibility from the
    backend and displays eligibility/issued state. This is not certificate issuance;
    backend PDF generation and verification remain future work. Source-level checks
    passed, but the Android build and live API behavior remain unverified.
    

    ## Backend certificate issuance (v3.3)
    Added idempotent certificate issuance, PDF download, and public verification
    endpoints. Issuance is gated by enrolment, all modules completed, and latest
    assessment passed. PDF uses a masked phone identifier pending verified learner
    name support. Runtime/PDF tests and Android download integration remain pending.
    

## v3.4 update — certificate eligibility consistency
The eligibility endpoint now reports whether a certificate has actually been issued, returns the certificate number when available, and uses status-appropriate notes. A source contract assertion was added. This does not mean runtime/API tests or Android build have passed.

## v3.8 certificate issuance UI integration
The Android source now includes the authenticated certificate issuance API, response DTO, repository call, and bilingual eligibility-gated Issue Certificate button. Source-level assertions passed. Android compilation, backend runtime integration, PDF download/open/share and device tests remain unverified. See `URJA_KENDRA_Android_Server_Assessment_v1.5/qa/CERTIFICATE_ISSUANCE_ANDROID_v3.8.md`.


## v5.0 update — assessment submission validation
The backend now rejects option IDs that do not belong to the selected question (HTTP 422). See `URJA_KENDRA_Backend_Starter_v0.4/qa/ASSESSMENT_SUBMISSION_VALIDATION_v5.0.md`. This is source-level validation; runtime tests and Android compilation remain unverified.
