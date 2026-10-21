# URJA KENDRA Developer Handoff v6.2

## Focus: executable OTP locale migration regression tests

Added `tests/test_otp_locale_migration.py` to exercise the migration against a real in-memory SQLite database. The tests cover: (1) adding the locale column and backfilling existing records to `en`, (2) safe repeated execution, and (3) a clear error when the OTP table is absent.

## Run

From `URJA_KENDRA_Backend_Starter_v0.4/` after installing requirements:

```bash
python -m pytest -q tests/test_otp_locale_migration.py
python -m pytest -q
```

## Verification status

The test cases are authored, but this handoff does not claim they passed in the current environment. Full backend tests still require all project dependencies (including `python-jose`). Android APK build remains unverified until CI runs.
