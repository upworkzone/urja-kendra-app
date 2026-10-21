# Database migrations

For an existing database, take a backup first, install `requirements.txt`, then run from this backend directory:

```bash
python -m migrations.add_otp_challenge_locale
```

This idempotent migration supports SQLite and PostgreSQL. It adds `otp_challenges.locale VARCHAR(2) NOT NULL DEFAULT 'en'`; existing rows receive English as a safe fallback. New OTP challenges store the selected `en` or `hi` locale. The migration refuses to run if the application schema has not been initialized and makes no changes if the column already exists.

For production, take a restorable database backup and run the migration during a controlled deployment window. This is a narrowly scoped migration, not a replacement for a full migration framework such as Alembic.
