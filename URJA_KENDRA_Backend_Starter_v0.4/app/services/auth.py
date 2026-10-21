from datetime import datetime, timedelta, timezone
import hmac
from uuid import uuid4
from sqlalchemy.orm import Session
from app.core.config import settings
from app.models.user import User
from app.models.otp_challenge import OtpChallenge
from app.services.otp import generate_otp, hash_otp, otp_expiry
from app.services.tokens import create_access_token

# Development stub only: OTP is logged to server console and must never be used in production.
class OtpRequestRateLimited(Exception):
    """Raised when too many OTP challenges were requested for one number."""

def request_otp(db: Session, phone_number: str, locale: str) -> tuple[str, str | None]:
    # SQLite stores server_default timestamps as naive UTC; compare with naive UTC.
    cutoff = datetime.utcnow() - timedelta(seconds=settings.otp_request_window_seconds)
    recent_requests = (
        db.query(OtpChallenge)
        .filter(OtpChallenge.phone_number == phone_number, OtpChallenge.created_at >= cutoff)
        .count()
    )
    if recent_requests >= settings.otp_max_requests_per_window:
        raise OtpRequestRateLimited()
    challenge_id = str(uuid4())
    otp = generate_otp()
    challenge = OtpChallenge(id=challenge_id, phone_number=phone_number, locale=locale, code_hash=hash_otp(challenge_id, otp), expires_at=otp_expiry(), attempts=0)
    db.add(challenge)
    db.commit()
    if settings.otp_provider == "stub" and settings.environment == "development":
        print(f"[DEV ONLY] URJA KENDRA OTP for {phone_number}: {otp}")
    return challenge_id, otp if settings.otp_provider == "stub" and settings.environment == "development" else None

def verify_otp(db: Session, phone_number: str, challenge_id: str, otp: str):
    challenge = db.get(OtpChallenge, challenge_id)
    now = datetime.now(timezone.utc)
    if not challenge or challenge.phone_number != phone_number or challenge.consumed_at is not None:
        return None
    expires = challenge.expires_at
    if expires.tzinfo is None:
        expires = expires.replace(tzinfo=timezone.utc)
    if now > expires or challenge.attempts >= settings.otp_max_attempts:
        return None
    challenge.attempts += 1
    if not hmac.compare_digest(hash_otp(challenge_id, otp), challenge.code_hash):
        db.commit()
        return None
    challenge.consumed_at = now
    user = db.query(User).filter(User.phone_number == phone_number).first()
    if not user:
        user = User(id=str(uuid4()), phone_number=phone_number, preferred_locale=challenge.locale, is_active=True)
        db.add(user)
    else:
        user.preferred_locale = challenge.locale
    db.commit()
    return user
