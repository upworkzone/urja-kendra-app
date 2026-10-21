# Verification next steps v2.1

1. Read `BACKEND_VERIFICATION_REPORT_v2.1.md` for the actual syntax and pytest results.
2. If pytest is blocked by a missing dependency, create a virtual environment and run:
   `python -m pip install -r requirements.txt`
   then `python -m pytest -q`.
3. If tests fail after dependencies install, fix the code or test assumptions and rerun.
4. Start the backend locally and execute `MANUAL_API_SMOKE_TESTS.md` against a disposable database.
5. Android integration and APK build remain separate, unverified release gates.
