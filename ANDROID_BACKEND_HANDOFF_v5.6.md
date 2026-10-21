# URJA KENDRA Developer Handoff v5.6

## Change in this milestone

Added a backend API regression test for OTP request throttling. It verifies three requests are accepted, the fourth request for the same phone within the configured window returns HTTP 429, and another phone number is not globally blocked.

## Validation status

This milestone adds a runtime test but does not claim it passed. Run the Backend tests GitHub Actions workflow to execute pytest with declared dependencies. Android build and device verification are still outstanding; no APK is claimed as verified.
