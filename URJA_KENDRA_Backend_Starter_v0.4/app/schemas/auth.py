from pydantic import BaseModel, Field

class OtpRequest(BaseModel):
    phone_number: str = Field(min_length=8, max_length=20)
    locale: str = Field(default="en", pattern="^(en|hi)$")

class OtpVerify(BaseModel):
    phone_number: str = Field(min_length=8, max_length=20)
    challenge_id: str
    otp: str = Field(min_length=4, max_length=8, pattern="^[0-9]+$")

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    expires_in: int

class UserOut(BaseModel):
    id: str
    phone_number: str
    preferred_locale: str
