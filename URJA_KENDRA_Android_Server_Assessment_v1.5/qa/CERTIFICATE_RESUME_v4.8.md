# Certificate resume state — v4.8

## Change

When a learner opens the server assessment screen, the app now makes a best-effort request for certificate eligibility in addition to loading the assessment. This allows a returning learner to see an already-issued certificate and reach PDF download/open/share controls without submitting the assessment again. The eligibility request is non-blocking: if it fails, assessment loading remains usable.

The old fallback copy claiming certificate issuance is not available was removed because it contradicted the implemented backend/API path.

## Source validation

Run:

```bash
python qa/check_certificate_resume.py
```

This is a source-contract check, not a Kotlin compilation or runtime test.

## Manual acceptance test

1. Sign in with a test account that is enrolled in a course.
2. Complete the course and pass its server-scored assessment.
3. Issue the certificate and confirm the certificate number appears.
4. Leave the assessment screen and reopen it.
5. Confirm the issued status and same certificate number appear without resubmitting answers.
6. Download the PDF; open and share it.
7. Repeat with an eligible account that has not yet issued the certificate; confirm the issue action appears.
8. Simulate a temporary eligibility endpoint failure; confirm the assessment itself can still load.
9. Repeat the visible status checks in Hindi and English.

## Limitations

Android Gradle build, API integration and device behaviour remain unverified until the GitHub Actions workflow is run and the app is installed on an emulator/device.
