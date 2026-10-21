# Manual API Smoke Tests — URJA KENDRA

Run against a local development server and a disposable test database only.
Do not use real learner data. Record the status and response for each step.

## A. Basic service
- [ ] `GET /health` returns HTTP 200 and `status: ok`.
- [ ] `GET /api/v1/courses?locale=en` returns course records.
- [ ] `GET /api/v1/courses?locale=hi` returns Hindi titles.
- [ ] `GET /api/v1/career-pathways?locale=hi` returns the four pathways.

## B. Development authentication
- [ ] Request OTP with a test phone number.
- [ ] Confirm the development response includes a challenge ID.
- [ ] Verify using the development-only OTP and obtain a token.
- [ ] Call `/api/v1/auth/me` with the token.
- [ ] Confirm a request without a token is rejected by protected routes.

**Do not ship while the OTP response exposes a development code.**

## C. Enrolment and progress
Using the authenticated token:
- [ ] Enrol in a valid course; verify the response.
- [ ] Enrol in the same course again; confirm duplicate handling is safe.
- [ ] Fetch `/api/v1/me/enrollments` and confirm the enrolment appears.
- [ ] Mark a module complete using the course progress endpoint.
- [ ] Fetch progress again and confirm the saved state persists.
- [ ] Try an invalid course/module ID and confirm a controlled error.

## D. Assessment
- [ ] Fetch the EV-01 assessment questions.
- [ ] Submit a complete set of valid answers.
- [ ] Confirm the server returns score, pass/fail and attempt identifier.
- [ ] Fetch the saved assessment attempts and confirm the attempt is present.
- [ ] Submit invalid question IDs/answer values; confirm rejection without a server error.
- [ ] Verify score calculation is performed server-side and answer keys are not exposed by the question endpoint.

## E. Certificate eligibility
- [ ] With incomplete modules, confirm eligibility is false.
- [ ] With completed modules but no passing attempt, confirm eligibility is false.
- [ ] With all required modules complete and a passing attempt, confirm eligibility is true.
- [ ] Confirm eligibility does not create or return a certificate file.

## F. Evidence
For every check record: date/time, backend commit/package, Python version, database type,
request, status code, sanitized response, result (PASS/FAIL/BLOCKED), and notes.
Never include OTPs, access tokens, private signing keys, or personal phone numbers in shared logs.
