# OTP rate-limit runtime regression test — v5.6

The API test `test_otp_request_rate_limit_returns_429_without_blocking_other_numbers` exercises the FastAPI endpoint using the isolated test database.

Expected behavior:

- The first three requests for the same phone number return HTTP 202 under default settings.
- The fourth request inside the configured 15-minute window returns HTTP 429.
- The blocked request does not prevent a different phone number from requesting an OTP.
- OTP code exposure is only enabled by the development-only stub configuration in pytest.

Run from `URJA_KENDRA_Backend_Starter_v0.4/`:

```bash
python -m pytest tests/test_api.py -q
```

A source-level check is not a substitute for a successful pytest run. The test must run in GitHub Actions or another environment with all `requirements.txt` dependencies installed.
