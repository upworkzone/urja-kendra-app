# Certificate lifecycle integration test — v5.3

Added `test_certificate_issue_is_idempotent_and_pdf_is_verifiable` to the backend API tests.

It exercises the authenticated public API for enrolment, completing all eight EV-01 modules,
issuing a certificate, repeat issuance, eligibility refresh, PDF download, and public verification.
A passing assessment attempt is inserted directly as test setup so the test isolates certificate
lifecycle behaviour; assessment submission/scoring is covered by a separate test.

Assertions include a stable certificate number on repeat issuance, `application/pdf`, a `%PDF-`
file signature, and successful public verification. This test must still be run in GitHub Actions
or another environment with all `requirements.txt` dependencies installed.
