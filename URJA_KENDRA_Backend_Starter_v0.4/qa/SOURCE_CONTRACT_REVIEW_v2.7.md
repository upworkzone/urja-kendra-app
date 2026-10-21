# Source contract review v2.7

## Fixes
- Removed a brittle test assertion that required the FastAPI application version to
  remain `0.3.0`, even though the backend app version was subsequently advanced.
  Application version metadata is not part of the learning endpoint contract.
- Added a source contract check that release security validation is inside the
  FastAPI lifespan function and runs before database initialization.

## Verification
- Python AST syntax parsing passed for all application and test Python files.
- The pytest suite has **not** been run in this environment; runtime dependency
  availability remains a blocker. Run the supplied `qa/run_backend_tests.sh` or
  `qa/run_backend_tests.ps1` on a machine with dependencies installed.
