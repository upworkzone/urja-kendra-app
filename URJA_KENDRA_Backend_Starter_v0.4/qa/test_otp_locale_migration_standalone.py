#!/usr/bin/env python3
"""Dependency-light executable checks for the OTP locale migration.

Run from the backend project directory:
    python qa/test_otp_locale_migration_standalone.py

This runner deliberately does not use pytest's conftest or import the API app,
so it can validate the migration even when an unrelated API dependency such as
python-jose is not installed. SQLAlchemy is still required.
"""
from importlib.util import module_from_spec, spec_from_file_location
from pathlib import Path
import sys

# Make backend package imports resolve when this script is launched by path.
BACKEND_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(BACKEND_ROOT))

from sqlalchemy import create_engine, inspect, text

MIGRATION_PATH = Path(__file__).resolve().parents[1] / "migrations" / "add_otp_challenge_locale.py"
spec = spec_from_file_location("otp_locale_migration_standalone", MIGRATION_PATH)
if spec is None or spec.loader is None:
    raise RuntimeError(f"Cannot load migration at {MIGRATION_PATH}")
migration = module_from_spec(spec)
spec.loader.exec_module(migration)


def check_backfill_and_column_addition():
    engine = create_engine("sqlite+pysqlite:///:memory:")
    try:
        with engine.begin() as conn:
            conn.execute(text("CREATE TABLE otp_challenges (id VARCHAR(36) PRIMARY KEY, phone_number VARCHAR(20))"))
            conn.execute(text("INSERT INTO otp_challenges (id, phone_number) VALUES ('old-1', '+910000000000')"))
        assert migration.migrate(engine) is True, "first migration should change schema"
        with engine.connect() as conn:
            names = {column["name"] for column in inspect(engine).get_columns("otp_challenges")}
            assert "locale" in names, "locale column was not added"
            value = conn.execute(text("SELECT locale FROM otp_challenges WHERE id='old-1'")).scalar_one()
            assert value == "en", f"existing row should default to en, got {value!r}"
    finally:
        engine.dispose()


def check_idempotence():
    engine = create_engine("sqlite+pysqlite:///:memory:")
    try:
        with engine.begin() as conn:
            conn.execute(text("CREATE TABLE otp_challenges (id VARCHAR(36) PRIMARY KEY, locale VARCHAR(2) NOT NULL DEFAULT 'en')"))
        assert migration.migrate(engine) is False, "existing locale column should be unchanged"
        assert migration.migrate(engine) is False, "repeated migration should remain unchanged"
    finally:
        engine.dispose()


def check_missing_table_error():
    engine = create_engine("sqlite+pysqlite:///:memory:")
    try:
        try:
            migration.migrate(engine)
        except RuntimeError as exc:
            assert "does not exist" in str(exc), f"unexpected error: {exc}"
        else:
            raise AssertionError("migration should fail clearly when the table is missing")
    finally:
        engine.dispose()


def main():
    checks = [
        ("column addition and existing-row backfill", check_backfill_and_column_addition),
        ("idempotent rerun", check_idempotence),
        ("missing-table error", check_missing_table_error),
    ]
    failed = 0
    for name, fn in checks:
        try:
            fn()
            print(f"PASS: {name}")
        except Exception as exc:
            failed += 1
            print(f"FAIL: {name}: {exc}", file=sys.stderr)
    print(f"Migration checks: {len(checks) - failed}/{len(checks)} passed")
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
