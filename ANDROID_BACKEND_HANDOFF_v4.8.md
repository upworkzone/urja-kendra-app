# URJA KENDRA Developer Handoff v4.8

## Milestone: returning learners can recover certificate state

- Assessment screen reloads certificate eligibility when it opens.
- Returning learners with an issued certificate can see its number and access the PDF download/open/share actions without submitting the quiz again.
- Eligibility lookup failure is best-effort and does not prevent the assessment from loading.
- Removed stale UI copy claiming certificate issuance is unavailable.
- Added `qa/check_certificate_resume.py` and a manual acceptance checklist.

## Checks run in this environment

- Android CI workflow source checker: 9 assertions passed.
- Release network security source checker: 8 assertions passed.
- Certificate resume source checker: 6 assertions passed.
- These checks inspect source/configuration only. Kotlin/Gradle compilation, backend runtime, real API behaviour and device testing were not run here.

## Next required action

Run `.github/workflows/android-build.yml` in GitHub Actions. If it succeeds, install the uploaded debug APK on an emulator or Android phone and execute the manual acceptance test in `URJA_KENDRA_Android_Server_Assessment_v1.5/qa/CERTIFICATE_RESUME_v4.8.md`. Do not treat this package as a verified APK.
