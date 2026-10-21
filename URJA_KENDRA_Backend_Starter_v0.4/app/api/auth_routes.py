from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.schemas.auth import OtpRequest, OtpVerify, TokenResponse, UserOut
from app.services.auth import OtpRequestRateLimited, request_otp, verify_otp
from app.services.tokens import create_access_token
from app.api.deps import current_user
from app.models.user import User
from app.core.config import settings

router = APIRouter(prefix="/api/v1/auth", tags=["auth"])

@router.post("/otp/request", status_code=202)
def otp_request(body: OtpRequest, db: Session = Depends(get_db)):
    try:
        challenge_id, dev_code = request_otp(db, body.phone_number, body.locale)
    except OtpRequestRateLimited:
        raise HTTPException(status_code=429, detail="Too many OTP requests. Please wait before trying again.")
    result = {"challenge_id": challenge_id, "message": "If this number can receive a code, an OTP has been sent."}
    # Only expose code in explicitly development-only stub mode to aid local testing.
    if settings.otp_provider == "stub" and settings.environment == "development":
        result["development_only_code"] = dev_code
    return result

@router.post("/otp/verify", response_model=TokenResponse)
def otp_verify(body: OtpVerify, db: Session = Depends(get_db)):
    user = verify_otp(db, body.phone_number, body.challenge_id, body.otp)
    if not user:
        raise HTTPException(status_code=400, detail="Invalid or expired OTP")
    token = create_access_token(user.id)
    return TokenResponse(access_token=token, expires_in=settings.access_token_minutes * 60)

@router.get("/me", response_model=UserOut)
def me(user: User = Depends(current_user)):
    return UserOut(id=user.id, phone_number=user.phone_number, preferred_locale=user.preferred_locale)
