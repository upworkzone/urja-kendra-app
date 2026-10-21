from sqlalchemy import String, DateTime, Integer, func
from sqlalchemy.orm import Mapped, mapped_column
from app.db.session import Base

class OtpChallenge(Base):
    __tablename__ = "otp_challenges"
    id: Mapped[str] = mapped_column(String(36), primary_key=True)
    phone_number: Mapped[str] = mapped_column(String(20), index=True)
    # Persist the locale chosen at OTP request time so verification can create/update the user correctly.
    locale: Mapped[str] = mapped_column(String(2), default="en", server_default="en")
    code_hash: Mapped[str] = mapped_column(String(128))
    expires_at: Mapped[object] = mapped_column(DateTime(timezone=True))
    attempts: Mapped[int] = mapped_column(Integer, default=0)
    consumed_at: Mapped[object | None] = mapped_column(DateTime(timezone=True), nullable=True)
    created_at: Mapped[object] = mapped_column(DateTime(timezone=True), server_default=func.now())
