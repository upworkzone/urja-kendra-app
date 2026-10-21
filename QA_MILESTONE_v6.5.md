# URJA KENDRA Developer Handoff v6.5

## Change
Added a backend API regression test for OTP brute-force resistance: after the configured five failed attempts, the challenge must reject even the correct code.

## Verification status
- Python source compilation: verified locally.
- Repository layout: verified locally.
- ZIP integrity: verified locally.
- Full pytest suite: not completed in this environment because `python-jose` is unavailable (`ModuleNotFoundError: jose`). The new regression test is added but is not claimed as executed.
- Android APK: not built or verified in this environment.

## Run the backend tests
From `URJA_KENDRA_Backend_Starter_v0.4`:

```bash
python -m pip install -r requirements.txt
python -m pytest -q -ra
```

Use a disposable test database. Do not use development OTP stub mode in production.
