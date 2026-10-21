# Assessment submission validation — v5.0

## Change
The EV-01 assessment API now rejects option IDs that are not valid for the submitted question, returning HTTP 422. Previously, an arbitrary option ID was simply scored as incorrect, which obscured malformed client requests.

## Preserved protections
- Requires exactly one answer per known question.
- Rejects duplicate question IDs.
- Calculates scores on the server.
- Does not return answer keys from the assessment GET endpoint.

## Verification
Run `python qa/check_assessment_submission_validation.py` from the backend project folder. This is a source-contract check and does not replace runtime API tests. Full pytest and Android build remain pending until the dependencies/build environment are available.
