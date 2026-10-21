# Backend test isolation — v5.4

The pytest `conftest.py` sets a unique temporary SQLite URL before importing the app, then resets all SQLAlchemy tables before and after each test. This prevents persistent local databases, reused phone numbers, enrolments, assessment attempts, and issued certificates from contaminating later test runs.

The test database uses development-only credentials and a stub OTP provider. Never use these values for deployment. A `qa/check_test_isolation.py` source checker validates the isolation wiring without importing runtime dependencies.

## Verify

From `URJA_KENDRA_Backend_Starter_v0.4/`:

```bash
python qa/check_test_isolation.py
python -m pytest -q -ra
```

The source checker does not replace the runtime pytest suite. Runtime tests require dependencies in `requirements.txt` and should be run in GitHub Actions or a configured local virtual environment.
