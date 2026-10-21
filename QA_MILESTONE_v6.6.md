# URJA KENDRA Developer Handoff v6.6

## Change
OTP hash verification now uses Python's `hmac.compare_digest` rather than ordinary string inequality. This reduces timing-leak risk when comparing the submitted OTP hash with the stored hash. It does not replace rate limits, attempt limits, HTTPS, or a production OTP provider.

## Verification status
- Constant-time comparison source guard: run locally for this package.
- Python source compilation: run locally for this package.
- Repository layout and ZIP integrity: run locally for this package.
- Full pytest suite: not completed because this environment cannot install the missing `python-jose` dependency (package index network unavailable).
- Android APK build: not built or verified.

## Run
From `URJA_KENDRA_Backend_Starter_v0.4`:

```bash
python qa/check_otp_constant_time_compare.py
python -m pip install -r requirements.txt
python -m pytest -q -ra
```
