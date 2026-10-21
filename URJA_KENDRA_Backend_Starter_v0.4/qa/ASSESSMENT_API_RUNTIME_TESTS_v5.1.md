# Assessment API runtime regression tests — v5.1

Added an integration test in `tests/test_api.py` that exercises the assessment endpoint through FastAPI's test client and the development OTP stub.

It verifies that:
- a signed-in learner can enrol in EV-01 and retrieve the assessment;
- an option ID that does not belong to a question is rejected with HTTP 422;
- duplicate question IDs are rejected with HTTP 422;
- a structurally valid complete answer set is accepted with HTTP 201 and returns a bounded score.

The test uses the development-only OTP stub and must never be run against production credentials or a production database. Runtime execution must be confirmed by CI; source compilation alone is not proof that this test passes.
