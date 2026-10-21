# URJA KENDRA Developer Handoff v5.4

## Change
Added isolated backend integration-test setup. Each pytest run gets a unique temporary SQLite database, and the schema is reset around each test. This addresses test pollution from persistent users, enrolments, assessment attempts, and certificates.

## Checks
- Source-level isolation checker added.
- Python source parsing and ZIP integrity checked during packaging.
- Full pytest runtime is still dependent on installing backend requirements; do not treat source checks as a passing runtime test suite.
- Android Gradle build and device installation remain unverified.

## Next action
Run `python qa/check_test_isolation.py`, then run `python -m pytest -q -ra` from the backend project directory or use GitHub Actions → Backend tests. Resolve any runtime failures before treating the backend as verified.
