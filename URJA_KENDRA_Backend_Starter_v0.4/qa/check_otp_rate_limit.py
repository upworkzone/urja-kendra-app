from pathlib import Path

root = Path(__file__).resolve().parents[1]
config = (root / "app" / "core" / "config.py").read_text()
auth = (root / "app" / "services" / "auth.py").read_text()
routes = (root / "app" / "api" / "auth_routes.py").read_text()
assert "otp_max_requests_per_window: int = 3" in config
assert "otp_request_window_seconds: int = 900" in config
assert "OtpChallenge.created_at >= cutoff" in auth
assert "raise OtpRequestRateLimited()" in auth
assert "except OtpRequestRateLimited" in routes
assert "status_code=429" in routes
print("PASS: OTP request limit defaults to 3 requests per phone per 15 minutes")
print("PASS: excess requests return HTTP 429 without creating another challenge")
print("NOTE: this is a source-contract check; runtime/API tests must still run in CI")
