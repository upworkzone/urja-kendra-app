# URJA KENDRA — Backend Verification Guide (v2.6)

This package adds repeatable backend test runners for macOS/Linux and Windows.
It does not claim that tests have passed; run them in an environment with Python
dependencies available.

## 1. Open a terminal in the backend folder

`URJA_KENDRA_Backend_Starter_v0.4`

## 2. Create and activate a virtual environment

### Windows PowerShell
```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt pytest httpx
.\qa\run_backend_tests.ps1
```

### macOS / Linux
```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt pytest httpx
./qa/run_backend_tests.sh
```

If dependency installation fails due to network access, retry on a machine with
internet access or use your organization's approved Python package mirror.

## 3. Run the end-to-end smoke test

Follow the existing backend README to start the development server, then run the
provided smoke test script from `qa/` against the local development instance.
Use only a disposable development database; the smoke test creates an enrolment,
marks lessons complete, and submits an assessment.

## 4. Report failures usefully

Please capture:
- The full command you ran
- The first traceback/error and the final exit code
- Python version and operating system
- Whether you used a clean development database

Do not paste production secrets, OTPs, tokens, or private user data into a chat.

## Verification boundary

The runners check Python compilation and execute pytest. They do not build the
Android app, validate real SMS delivery, prove production security, or certify
course content. Android build and device testing remain separate release gates.
