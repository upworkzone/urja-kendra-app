"""Idempotent schema migration for persisting OTP-request language.

Run from the backend project directory after installing requirements:
    python -m migrations.add_otp_challenge_locale

Supports SQLite and PostgreSQL. Back up the database before production migrations.
"""
from sqlalchemy import create_engine, inspect, text
from app.core.config import settings


def migrate(engine) -> bool:
    """Add otp_challenges.locale when missing; return True if changed."""
    dialect = engine.dialect.name
    if dialect not in {"sqlite", "postgresql"}:
        raise RuntimeError(f"Unsupported database dialect for this migration: {dialect}")

    inspector = inspect(engine)
    if "otp_challenges" not in inspector.get_table_names():
        raise RuntimeError(
            "Table 'otp_challenges' does not exist. Initialize the application schema first."
        )
    columns = {column["name"] for column in inspector.get_columns("otp_challenges")}
    if "locale" in columns:
        return False

    # Constant server default backfills existing rows safely and preserves English as fallback.
    with engine.begin() as connection:
        connection.execute(text(
            "ALTER TABLE otp_challenges "
            "ADD COLUMN locale VARCHAR(2) NOT NULL DEFAULT 'en'"
        ))
    return True


def main() -> None:
    engine = create_engine(settings.database_url, pool_pre_ping=True)
    try:
        changed = migrate(engine)
        print("PASS: added otp_challenges.locale (existing challenges default to 'en')" if changed
              else "PASS: otp_challenges.locale already exists; no change needed")
    finally:
        engine.dispose()


if __name__ == "__main__":
    main()
