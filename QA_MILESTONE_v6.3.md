# URJA KENDRA QA milestone v6.3

## Change
Added `URJA_KENDRA_Backend_Starter_v0.4/qa/test_otp_locale_migration_standalone.py`, a direct executable SQLite regression runner for the OTP challenge locale migration. It does not import the FastAPI app or rely on pytest's `conftest.py`, so an unrelated missing API dependency such as `python-jose` no longer prevents these migration-specific checks from running.

## Verified in this workspace
- Standalone SQLite migration checks: 3/3 passed.
- Backend Python source compilation: passed.
- ZIP integrity: passed.

## Not verified
- Full backend pytest suite: blocked in this environment because `python-jose` is not installed.
- Android Gradle build / APK: not run successfully in this workspace.
- PostgreSQL migration execution: not exercised against a live PostgreSQL server; the migration code supports the dialect, but only SQLite was tested here.

## Run migration checks
From the backend project directory:

```bash
python qa/test_otp_locale_migration_standalone.py
```

For the full backend suite, install dependencies first:

```bash
pip install -r requirements.txt
python -m pytest -q
```

Back up production databases before applying migrations.
