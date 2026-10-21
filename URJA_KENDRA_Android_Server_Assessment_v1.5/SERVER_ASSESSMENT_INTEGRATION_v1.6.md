# URJA KENDRA v1.6 — Server assessment flow integration

## Changes in this package
- Connected the EV-01 lesson screen's assessment action to `ServerAssessmentScreen`, replacing the device-scored pilot assessment path.
- Fixed a malformed literal `\n` in `ServerAssessmentScreen.kt` that would prevent Kotlin compilation.
- Removed an unused file-level mutable submission variable; submission intent remains composition state.

## Expected flow
1. Learner signs in.
2. Learner enrols in EV-01.
3. Learner opens lessons and selects **Start EV-01 assessment**.
4. Android fetches questions and submits answer IDs to backend v0.4.
5. Server-returned score/pass status is displayed.

## Known limitations / verification
- This is source-only. No Gradle executable/wrapper is present in this environment, so Android compilation and emulator tests have not been run.
- The backend v0.4 test suite was previously blocked by a missing `python-jose` dependency. Verify endpoint paths and response models against the running backend before use.
- Server assessment currently wired only for course ID `ev-01`; other courses need their own reviewed assessment content.
- Do not issue certificates until server-side eligibility, identity, course completion, assessment attempts, and certificate verification are tested.
- The app's local development HTTP base URL is for emulator-only use; production requires HTTPS.
