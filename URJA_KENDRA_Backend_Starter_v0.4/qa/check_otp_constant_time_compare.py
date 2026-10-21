#!/usr/bin/env python3
"""Source-level guard: OTP hash verification must use constant-time comparison."""
from pathlib import Path

source = Path(__file__).resolve().parents[1] / "app" / "services" / "auth.py"
text = source.read_text(encoding="utf-8")
assert "import hmac" in text, "OTP service must import hmac"
assert "hmac.compare_digest(hash_otp(challenge_id, otp), challenge.code_hash)" in text, (
    "OTP verification must compare hashes with hmac.compare_digest"
)
assert "hash_otp(challenge_id, otp) != challenge.code_hash" not in text, (
    "Timing-sensitive direct string comparison must not return"
)
print("PASS: OTP hash verification uses hmac.compare_digest")
