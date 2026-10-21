# URJA KENDRA Developer Handoff v6.1

## Change
Added an idempotent database migration for the OTP challenge locale introduced in v6.1. This closes the documented upgrade gap for existing SQLite/PostgreSQL installations: old OTP rows default to English and new challenges can persist Hindi/English selection.

## Verification
- Migration module syntax compilation: checked in the package QA run.
- Migration regression tests cover adding/backfilling the column, idempotent rerun, and missing-schema failure.
- These tests require SQLAlchemy and must be run in the backend virtual environment with `python -m pytest -q`.
- This milestone does not claim the full backend suite or Android APK build passed.

## Upgrade
Back up the database, install backend requirements, and run from `URJA_KENDRA_Backend_Starter_v0.4`:

```bash
python -m migrations.add_otp_challenge_locale
```

Supports SQLite and PostgreSQL only; refuses other dialects. Review and test against a staging backup before production use.
