# URJA KENDRA Developer Handoff v6.7 — Test environment diagnostics

## Change
- Backend test runners on Bash and PowerShell now identify each missing Python import separately rather than reporting only a generic dependency failure.
- Runners provide a requirements installation command and a local-wheelhouse fallback for offline environments.
- Removed generated Python bytecode/cache directories from the handoff package.

## Verification in this workspace
- Python source compilation: PASS.
- Repository layout checker: PASS.
- SQLite OTP locale migration standalone tests: PASS (3/3).
- Backend test runner diagnostic: PASS; it explicitly reported missing modules `psycopg`, `jose`, and `passlib` and exited with code 2 without attempting network installs.
- Repository layout checks: PASS (4/4).
- ZIP integrity: validated after packaging.

## Important limits
This is test-runner diagnostics, not a claim that backend runtime tests or the Android APK build passed. The full suite must be run in an environment with all dependencies installed. Android CI build remains unverified until GitHub Actions completes successfully.
