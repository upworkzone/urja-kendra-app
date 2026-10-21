# v1.1 — Enrolment and My Learning source integration

## Included
- Course detail now exposes an Enrol button.
- Enrolment calls the backend endpoint using the backend course ID from the live catalogue.
- My Learning calls the authenticated enrolments endpoint and shows progress percentages.
- Loading, empty, retry and sign-in-required states are included.

## Known limitations
- The course detail screen is still a summary and does not yet show actual lessons or a module completion workflow.
- The Open Course action uses the existing detail route and needs proper backend-ID-based detail navigation.
- Backend API response and authentication must be verified. The Android app is still source-only and has not been compiled or emulator-tested.
- My Learning requires a valid stored token; unauthenticated state currently displays a sign-in prompt but does not automatically navigate to the login screen.
- Production requires HTTPS, real OTP provider, backend hardening, and testing.
