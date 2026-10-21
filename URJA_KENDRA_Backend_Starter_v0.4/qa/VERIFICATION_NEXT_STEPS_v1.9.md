# Verification v1.9 — next action

This package records the current backend test-collection blocker and includes test runner scripts.
The test run in the report was attempted in the current environment; it was blocked because the
`jose` import was unavailable. No tests are claimed as passing.

- Windows: run `qa\\run_backend_tests_windows.bat` from the backend project root.
- macOS/Linux: run `bash qa/run_backend_tests.sh` from the backend project root.
- If dependencies are installed in a virtual environment, activate it first.
- Preserve the actual pytest output and update the report only after a real rerun.
