# URJA KENDRA Backend Starter v0.4
FastAPI + SQLAlchemy development backend with bilingual catalogue, OTP development stub, enrolment, lesson progress, persisted EV-01 assessment attempts, and certificate eligibility check (no certificate issuance).

See `ASSESSMENT_BACKEND_v0.4.md` for routes, scope, run instructions, and safety limitations. Not production-ready.


## Security hardening v2.0
Startup checks reject unsafe default signing secrets and stub OTP in non-development environments. See `qa/SECURITY_HARDENING_v2.0.md`. This is not a production security certification; automated tests have not been verified in this package.


## Reproducible local setup v2.2
See `qa/LOCAL_ENVIRONMENT_v2.2.md`. A Dockerfile and Compose configuration are included for a local development environment; they have not been built or run in this environment.


## End-to-end API smoke test v2.3
See `qa/E2E_SMOKE_TEST_v2.3.md` and run `python qa/run_e2e_smoke_test.py` against a local development server. This mutates local test data and uses the development OTP stub only.


### Certificate status consistency (v3.4)
The certificate-eligibility endpoint now reflects an already-issued certificate and returns its certificate number. See `qa/CERTIFICATE_ELIGIBILITY_CONSISTENCY_v3.4.md`. Runtime verification remains pending.


### Certificate lifecycle regression coverage (v5.2)

Backend integration tests now verify that certificate issuance is blocked until course requirements are met, a PDF cannot be downloaded before issuance, unknown public verification numbers are invalid, and learner-specific certificate routes require authentication. Run with `python -m pytest -q` after installing `requirements.txt`. These tests are added but were not fully executed in the current offline environment because `python-jose` is unavailable.


## OTP request limits (v5.5)
By default, no more than 3 OTP challenges may be requested for one phone number within 15 minutes. Excess requests return HTTP 429. Configure with `OTP_MAX_REQUESTS_PER_WINDOW` and `OTP_REQUEST_WINDOW_SECONDS`. Run `python qa/check_otp_rate_limit.py` for a source-contract check. Runtime behavior still requires the backend CI test run.

### OTP language preference fix (v6.0)

The locale selected in the OTP request is now stored on the OTP challenge and applied to the user when the OTP is verified. This fixes the prior behavior where verification always created/updated the profile as English. A regression test covers the Hindi (`hi`) flow.

**Database note:** `Base.metadata.create_all()` creates the new `locale` column for fresh databases but does not alter an existing `otp_challenges` table. Before upgrading a persistent database, apply a reviewed schema migration that adds `locale VARCHAR(2) NOT NULL DEFAULT 'en'`; do not rely on `create_all()` to migrate existing tables.
