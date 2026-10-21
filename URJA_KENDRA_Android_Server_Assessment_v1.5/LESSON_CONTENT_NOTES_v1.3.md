# Lesson Content v1.3 — source-only milestone

## Added
- A bilingual (English/Hindi) introductory lesson viewer for seeded backend course `ev-01` / code `EV-01`.
- Eight module summaries based on the approved pilot course content pack, with one knowledge-check question and suggested answer per module.
- Expand/collapse lesson cards; existing server-backed progress marking remains in place.
- Explicit learner-safety messaging: this is introductory awareness content, not authorization to open, service or repair high-voltage systems.

## Important limits
- This is an Android source ZIP, not an APK. No Gradle build or emulator run was available/verified in this environment.
- Detailed content is only wired for `ev-01`. Other courses show progress controls but no detailed lesson content yet.
- Knowledge checks are static self-checks, not scored assessments. Certificate issuance is not implemented.
- Text is a concise app adaptation of the EV-01 pilot pack, not a substitute for instructor-led practice, OEM instructions, qualified technical review, or safety approval.
- Existing API base URL and development OTP limitations remain unchanged. Do not use with real users until HTTPS, real OTP delivery, secret management, rate limiting, production deployment and end-to-end testing are completed.

## Next milestone
Implement scored assessment with a configurable pass threshold, attempt handling, and server-side eligibility rules. Certificate generation should follow only after course-specific content and assessment policy are reviewed and approved.
