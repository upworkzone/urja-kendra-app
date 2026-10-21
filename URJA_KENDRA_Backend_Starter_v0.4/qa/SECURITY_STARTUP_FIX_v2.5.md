# URJA KENDRA Backend Security Startup Fix v2.5

## Finding
The API configured a FastAPI `lifespan` handler and also registered
`@app.on_event("startup")` for release-security validation. FastAPI lifespan
handling replaces legacy startup/shutdown event handlers; the validation
therefore was not reliably part of application startup.

## Change
`settings.validate_release_security()` now runs as the first operation inside
the lifespan handler, before database creation or course seeding. The legacy
startup event was removed. API version string is now `0.4.1`.

## Expected behavior
- Development configuration may use the documented development OTP stub.
- Non-development configuration must use a unique `AUTH_SECRET_KEY` of at
  least 32 characters and a non-stub OTP provider.
- Invalid release configuration should prevent the application from starting.

## Verification status
- Python source syntax is checked in the packaging step.
- Full application tests and runtime startup checks have **not** been executed
  here because the Python runtime dependencies were unavailable in this
  environment. Run `qa/run_backend_tests.sh` and the E2E smoke test locally
  before treating this as verified.
