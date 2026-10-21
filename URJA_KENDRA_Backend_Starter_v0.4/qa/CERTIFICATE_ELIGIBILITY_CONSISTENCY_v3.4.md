# Certificate eligibility consistency — v3.4

## Change
The existing `GET /api/v1/me/courses/{course_id}/certificate-eligibility` endpoint now checks the certificate table. It reports `certificate_issued: true` and the certificate number when a record exists, and chooses a note based on whether the learner is already issued, eligible to issue, or still needs to complete requirements.

## Contract fields
- `eligible_for_certificate`: all configured modules completed and latest assessment passed.
- `certificate_issued`: a certificate record exists for this user and course.
- `certificate_number`: certificate number when issued, otherwise `null`.
- `note`: status message aligned to the state.

## Verification status
Source contract assertions and Python AST syntax parsing are run when available. This is not a substitute for pytest against installed dependencies, API runtime tests, database migration testing, PDF rendering checks, or an Android build. Existing databases may require a migration to create the certificate table.
