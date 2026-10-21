# Certificate issuance integration — v3.8

## Added
- Retrofit `POST /api/v1/me/courses/{courseId}/certificate` call with bearer authentication.
- Response DTO for certificate number, issued status, and download path.
- Repository method for certificate issuance.
- Bilingual Issue Certificate action shown only when the server reports eligibility and no certificate has been issued.
- Issuance runs in a Compose `LaunchedEffect`; success shows the returned certificate number and refreshes eligibility.
- Failure is surfaced in the existing screen error area, and the issuing state resets so the learner can retry.

## Verification status
- Source-level contract assertions can confirm endpoint, model fields, eligibility gate, bilingual button and issuance error handling.
- Android Gradle compilation and device/API integration tests have not been run in this environment: Gradle and the Gradle wrapper are unavailable. Do not treat this as a verified APK.

## Follow-up
- Integrate PDF download and open/share intents against the issued certificate endpoint.
- Add UI tests for eligible/ineligible, already-issued, network-failure, and repeat-tap states.
- Validate certificate response/error contracts against a running backend.
