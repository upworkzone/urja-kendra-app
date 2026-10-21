# URJA KENDRA Backend QA Harness v1.8

This package is a test-runner and verification guide layered on top of backend v0.4.
It is **not** a production-ready backend and does not certify that tests pass.

## Run locally

1. Use Python 3.11+ in a virtual environment.
2. From this directory, install the project's requirements:
   `pip install -r requirements.txt`
3. Run:
   `python -m pytest -q`

If the test run fails because a dependency is missing, install the pinned dependencies from
`requirements.txt` and rerun. Record the actual output; do not mark failures as passed.

## Scope

- Existing automated tests cover health, bilingual catalogue, career pathways and the development OTP flow.
- Contract tests inspect registration of learning routes and constraints.
- `qa/MANUAL_API_SMOKE_TESTS.md` contains a manual verification sequence for enrolment, progress,
  assessment submission, saved results, and certificate eligibility.
- Tests are not a substitute for a dedicated test database, security review, or real-device testing.

## Blocking safety notes

- OTP response exposes a development-only code. Never expose that in production.
- Replace development signing secrets with managed environment secrets.
- Use HTTPS, SMS-provider OTP delivery, expiry, attempt throttling, and abuse monitoring.
- Configure an isolated test database before tests that mutate persistent state.
- Certificate eligibility is not certificate issuance.
