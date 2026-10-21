import hashlib, hmac, secrets, string
from datetime import datetime, timedelta, timezone
from app.core.config import settings

def generate_otp() -> str:
    return "".join(secrets.choice(string.digits) for _ in range(6))

def hash_otp(challenge_id: str, otp: str) -> str:
    return hmac.new(settings.auth_secret_key.encode(), f"{challenge_id}:{otp}".encode(), hashlib.sha256).hexdigest()

def otp_expiry():
    return datetime.now(timezone.utc) + timedelta(seconds=settings.otp_ttl_seconds)
