# URJA KENDRA QA Milestone v6.4

## Change
Added API regression tests for OTP challenge replay protection and phone-number binding. The tests assert that a consumed challenge cannot mint another token, and a challenge submitted with the wrong phone cannot authenticate that number or consume the legitimate challenge.

## Checks
- Python source compilation: run locally with `python -m compileall -q URJA_KENDRA_Backend_Starter_v0.4/app URJA_KENDRA_Backend_Starter_v0.4/tests`.
- Full pytest: not run successfully in this workspace because the environment lacks `python-jose` and package installation cannot reach the package index. Run `pip install -r requirements.txt` in an internet-enabled environment, then `python -m pytest -q`.
- Android APK build: not verified.

These are regression tests added to the suite; they are not represented as passing until the full suite is run.
