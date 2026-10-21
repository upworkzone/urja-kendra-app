# URJA KENDRA Developer Handoff v5.2

## Milestone
Added backend integration tests for certificate lifecycle guardrails.

## Included
- Eligibility remains false for a newly enrolled learner who has not completed the course and passed assessment.
- Certificate issuance returns HTTP 409 before eligibility.
- Certificate PDF download returns HTTP 404 before issuance.
- Unknown public certificate numbers verify as invalid.
- Authenticated learner certificate endpoints reject unauthenticated requests.
- Added `qa/check_certificate_lifecycle_contract.py` for source-level regression checks.

## Verification status
- Source contract checks and Python syntax parsing can be run locally.
- Full pytest is currently blocked in this environment by missing `python-jose`; dependency installation was not available.
- GitHub Actions and Android Gradle build have not been executed from this session.
- No verified APK is included.

## Run
From `URJA_KENDRA_Backend_Starter_v0.4/` after installing dependencies:

```bash
python -m pytest -q
python -m compileall -q app tests
python qa/check_certificate_lifecycle_contract.py
```
