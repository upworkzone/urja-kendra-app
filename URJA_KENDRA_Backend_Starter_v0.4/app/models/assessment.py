from sqlalchemy import String, DateTime, Integer, Boolean, ForeignKey, JSON, func
from sqlalchemy.orm import Mapped, mapped_column
from app.db.session import Base

class AssessmentAttempt(Base):
    __tablename__ = "assessment_attempts"
    id: Mapped[str] = mapped_column(String(36), primary_key=True)
    user_id: Mapped[str] = mapped_column(ForeignKey("users.id", ondelete="CASCADE"), index=True)
    course_id: Mapped[str] = mapped_column(ForeignKey("courses.id", ondelete="RESTRICT"), index=True)
    score_percent: Mapped[int] = mapped_column(Integer)
    correct_count: Mapped[int] = mapped_column(Integer)
    question_count: Mapped[int] = mapped_column(Integer)
    passed: Mapped[bool] = mapped_column(Boolean, default=False)
    threshold_percent: Mapped[int] = mapped_column(Integer, default=70)
    answers_json: Mapped[dict] = mapped_column(JSON)
    submitted_at: Mapped[object] = mapped_column(DateTime(timezone=True), server_default=func.now())
