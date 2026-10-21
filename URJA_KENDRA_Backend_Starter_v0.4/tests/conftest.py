"""Isolate every API test in a disposable SQLite database.

This module runs before pytest imports test modules, so the database URL is
set before app settings and SQLAlchemy's engine are constructed.
"""
import atexit
import os
import tempfile
import uuid
from pathlib import Path

_TEST_DB = Path(tempfile.gettempdir()) / f"urja_kendra_pytest_{uuid.uuid4().hex}.db"
os.environ["ENVIRONMENT"] = "development"
os.environ["DATABASE_URL"] = f"sqlite:///{_TEST_DB.as_posix()}"
os.environ["AUTH_SECRET_KEY"] = "PYTEST-ONLY-SECRET-NOT-FOR-DEPLOYMENT-000000"
os.environ["OTP_PROVIDER"] = "stub"


def _cleanup_test_database() -> None:
    try:
        _TEST_DB.unlink(missing_ok=True)
    except OSError:
        # Windows or a lingering SQLite handle may delay cleanup; the file is
        # uniquely named per run and contains test-only data.
        pass


atexit.register(_cleanup_test_database)

# Import the application once so all model classes are registered in metadata.
from app.main import app as _app  # noqa: E402,F401
from app.db.session import Base, engine  # noqa: E402
import pytest  # noqa: E402


@pytest.fixture(autouse=True)
def reset_database_between_tests():
    """Prevent users, enrollments, attempts, and certificates leaking across tests."""
    Base.metadata.drop_all(bind=engine)
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)
