# End-to-end API smoke test v2.3

This script exercises the local API from health check through EV-01 certificate eligibility:
health, bilingual catalogue, development OTP login, profile, enrolment, lesson progress,
server-side assessment submission, and eligibility. It uses a developer-only answer fixture
to submit a passing assessment; the public API must not return answer keys.

## Run it

1. Install requirements and start the API from this directory:
   `python -m pip install -r requirements.txt`
   `uvicorn app.main:app --reload --host 127.0.0.1 --port 8000`
2. In a second terminal, from this same project directory:
   `python qa/run_e2e_smoke_test.py`

If your API runs elsewhere, set `URJA_API_BASE_URL`, for example
`http://127.0.0.1:8001`. You can set `URJA_TEST_PHONE` to a disposable test phone string.

## Expected behavior

On a correctly configured local dev server, it should report PASS for each stage and exit 0.
If it reports a failure, preserve the actual output and fix the issue before rerunning.
The script does not delete or reset the local database; it creates or reuses a test user,
enrolment and progress records. Use a disposable development database.

## Safety

- Development OTP stub only; do not use this against a production server.
- The script marks all eight EV-01 modules complete and submits a passing test attempt.
- It does not issue a certificate; it only checks the eligibility endpoint.
- This script has been packaged but has not been run against a live backend in this environment.
