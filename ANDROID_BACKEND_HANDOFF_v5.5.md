# URJA KENDRA Developer Handoff v5.5

## Change: OTP request throttling
- Backend limits OTP challenge creation to 3 requests per phone number per 15-minute window by default.
- Excess requests return HTTP 429 and do not create a new challenge.
- Limits are configurable with `OTP_MAX_REQUESTS_PER_WINDOW` and `OTP_REQUEST_WINDOW_SECONDS`; non-development startup validates safe bounds.
- Existing per-challenge verification-attempt limits remain in place.

## Check
Run `python qa/check_otp_rate_limit.py` from `URJA_KENDRA_Backend_Starter_v0.4/`. This is a source-level contract check, not a runtime test. Run the Backend tests GitHub Actions workflow to verify behavior with dependencies installed.

## Important limitations
This per-phone database limit is a baseline, not complete abuse prevention. Production should also add IP/device throttling, monitoring, provider-side controls, phone normalization, and a real OTP provider. No Android APK build is claimed or verified.
