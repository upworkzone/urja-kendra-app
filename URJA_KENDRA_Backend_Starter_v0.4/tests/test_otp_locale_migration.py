"""Regression tests for the OTP locale schema migration."""
import importlib.util
from pathlib import Path

import pytest
from sqlalchemy import create_engine, text, inspect

MIGRATION_PATH = Path(__file__).parents[1] / "migrations" / "add_otp_challenge_locale.py"
spec = importlib.util.spec_from_file_location("otp_locale_migration", MIGRATION_PATH)
migration = importlib.util.module_from_spec(spec)
spec.loader.exec_module(migration)

def make_engine():
    return create_engine("sqlite+pysqlite:///:memory:")

def test_migration_adds_locale_and_backfills_existing_rows():
    engine = make_engine()
    with engine.begin() as conn:
        conn.execute(text("CREATE TABLE otp_challenges (id VARCHAR(36) PRIMARY KEY, phone_number VARCHAR(20))"))
        conn.execute(text("INSERT INTO otp_challenges (id, phone_number) VALUES ('old-1', '+910000000000')"))
    assert migration.migrate(engine) is True
    with engine.connect() as conn:
        columns = {c["name"] for c in inspect(engine).get_columns("otp_challenges")}
        assert "locale" in columns
        assert conn.execute(text("SELECT locale FROM otp_challenges WHERE id='old-1'")).scalar_one() == "en"
    engine.dispose()

def test_migration_is_idempotent():
    engine = make_engine()
    with engine.begin() as conn:
        conn.execute(text("CREATE TABLE otp_challenges (id VARCHAR(36) PRIMARY KEY, locale VARCHAR(2) NOT NULL DEFAULT 'en')"))
    assert migration.migrate(engine) is False
    assert migration.migrate(engine) is False
    engine.dispose()

def test_migration_fails_clearly_when_table_missing():
    engine = make_engine()
    with pytest.raises(RuntimeError, match="does not exist"):
        migration.migrate(engine)
    engine.dispose()
