from sqlalchemy import String, Integer, Boolean
from sqlalchemy.orm import Mapped, mapped_column
from app.db.session import Base

class Course(Base):
    __tablename__ = "courses"
    id: Mapped[str] = mapped_column(String(32), primary_key=True)
    code: Mapped[str] = mapped_column(String(20), unique=True, index=True)
    category: Mapped[str] = mapped_column(String(24), index=True)
    title_en: Mapped[str] = mapped_column(String(200))
    title_hi: Mapped[str] = mapped_column(String(200))
    summary_en: Mapped[str] = mapped_column(String(1000))
    summary_hi: Mapped[str] = mapped_column(String(1000))
    module_count: Mapped[int] = mapped_column(Integer, default=8)
    published: Mapped[bool] = mapped_column(Boolean, default=True)
