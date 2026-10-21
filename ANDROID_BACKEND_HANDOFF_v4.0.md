# URJA KENDRA Developer Handoff v4.0

## Milestone
Android certificate PDF download/open/share source integration added.

## Changes
- Added authenticated streaming PDF endpoint to Retrofit API and repository.
- Added download action that saves the server PDF in app-private certificate storage.
- Added FileProvider configuration and Open PDF / Share PDF actions.
- Added optional `certificate_number` to eligibility model to show already-issued certificates after a refresh.
- Added QA checklist at `URJA_KENDRA_Android_Server_Assessment_v1.5/qa/CERTIFICATE_PDF_ANDROID_v4.0.md`.

## Validation
Source-level checks and XML parsing can be run locally; Android compilation, live API checks, PDF rendering, and device tests are not verified. No APK is included.

## Known release blockers
- No Gradle wrapper and no system Gradle/Android SDK were available in the prior environment check.
- Backend runtime tests require dependencies and database migration testing.
- Replace development base URL and disable cleartext HTTP before release.
- Real OTP provider, production signing, issuer wording, and certificate legal review remain outstanding.
