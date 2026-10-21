# URJA KENDRA Developer Handoff v6.0 — OTP locale persistence

## Source change
- OTP challenge now stores the requested UI locale (`en` or `hi`).
- OTP verification applies the locale stored on that challenge instead of hardcoding English at the API boundary.
- Added an API regression test that requests a Hindi OTP, verifies it, then confirms `/me` returns `preferred_locale: hi`.

## Checks run for this package
- Python source compilation: run with `python -m compileall -q app tests`.
- Python test suite: attempted separately where dependencies permit. A passing runtime test result must not be inferred from syntax compilation alone.
- Android APK build: not run/verified in this workspace.

## Upgrade warning
For an existing database, `create_all()` will not add the new column. Apply a reviewed migration adding `locale VARCHAR(2) NOT NULL DEFAULT 'en'` to `otp_challenges` before deploying the updated code to a persistent database. Fresh test databases create the column from model metadata.

## Next verification
Run `pip install -r requirements.txt` then `python -m pytest -q` from `URJA_KENDRA_Backend_Starter_v0.4`. Run the GitHub Android workflow independently; no APK is included or claimed verified by this milestone.
