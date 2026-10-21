from pathlib import Path

root = Path(__file__).resolve().parents[1]
conftest = (root / "tests" / "conftest.py").read_text()
workflow = (root.parent / ".github" / "workflows" / "backend-tests.yml").read_text()
assert 'DATABASE_URL"] = f"sqlite:///{_TEST_DB.as_posix()}"' in conftest
assert "Base.metadata.drop_all(bind=engine)" in conftest
assert "Base.metadata.create_all(bind=engine)" in conftest
assert "python -m pytest -q -ra" in workflow
print("PASS: disposable SQLite database configured before app import")
print("PASS: schema reset around each test")
print("PASS: backend CI runs pytest")
