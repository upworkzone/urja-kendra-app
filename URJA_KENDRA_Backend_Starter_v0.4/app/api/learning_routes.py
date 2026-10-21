from datetime import datetime, timezone
from uuid import uuid4
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import func
from app.api.deps import current_user
from app.db.session import get_db
from app.models.user import User
from app.models.course import Course
from app.models.learning import Enrollment, LessonProgress
from app.schemas.learning import EnrollmentOut, ProgressIn, ProgressOut

router = APIRouter(prefix="/api/v1", tags=["learning"])

def enrollment_payload(db: Session, enrollment: Enrollment, locale: str) -> dict:
    course = db.get(Course, enrollment.course_id)
    completed = db.query(func.count(LessonProgress.id)).filter(
        LessonProgress.user_id == enrollment.user_id,
        LessonProgress.course_id == enrollment.course_id,
        LessonProgress.status == "COMPLETED").scalar() or 0
    total = course.module_count
    return {"id": enrollment.id, "course_id": course.id, "course_code": course.code,
            "course_title": course.title_hi if locale == "hi" else course.title_en,
            "status": enrollment.status, "enrolled_at": enrollment.enrolled_at,
            "completed_modules": completed, "total_modules": total,
            "progress_percent": int(completed * 100 / total) if total else 0}

@router.post("/courses/{course_id}/enroll", response_model=EnrollmentOut, status_code=201)
def enroll(course_id: str, locale: str = "en", user: User = Depends(current_user), db: Session = Depends(get_db)):
    if locale not in ("en", "hi"):
        raise HTTPException(status_code=422, detail="locale must be en or hi")
    course = db.get(Course, course_id)
    if not course or not course.published:
        raise HTTPException(status_code=404, detail="Course not found")
    enrollment = db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first()
    if enrollment is None:
        enrollment = Enrollment(id=str(uuid4()), user_id=user.id, course_id=course_id, status="ACTIVE")
        db.add(enrollment)
        db.commit()
        db.refresh(enrollment)
    return enrollment_payload(db, enrollment, locale)

@router.get("/me/enrollments", response_model=list[EnrollmentOut])
def my_enrollments(locale: str = "en", user: User = Depends(current_user), db: Session = Depends(get_db)):
    if locale not in ("en", "hi"):
        raise HTTPException(status_code=422, detail="locale must be en or hi")
    rows = db.query(Enrollment).filter_by(user_id=user.id).order_by(Enrollment.enrolled_at.desc()).all()
    return [enrollment_payload(db, row, locale) for row in rows]

@router.get("/courses/{course_id}/progress", response_model=list[ProgressOut])
def get_progress(course_id: str, user: User = Depends(current_user), db: Session = Depends(get_db)):
    enrolled = db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first()
    if not enrolled:
        raise HTTPException(status_code=403, detail="Enroll in this course first")
    return db.query(LessonProgress).filter_by(user_id=user.id, course_id=course_id).order_by(LessonProgress.module_number).all()

@router.put("/courses/{course_id}/progress", response_model=ProgressOut)
def update_progress(course_id: str, payload: ProgressIn, user: User = Depends(current_user), db: Session = Depends(get_db)):
    course = db.get(Course, course_id)
    if not course or not course.published:
        raise HTTPException(status_code=404, detail="Course not found")
    if payload.module_number > course.module_count:
        raise HTTPException(status_code=422, detail="module_number exceeds course module count")
    enrolled = db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first()
    if not enrolled:
        raise HTTPException(status_code=403, detail="Enroll in this course first")
    row = db.query(LessonProgress).filter_by(user_id=user.id, course_id=course_id, module_number=payload.module_number).first()
    if row is None:
        row = LessonProgress(id=str(uuid4()), user_id=user.id, course_id=course_id, module_number=payload.module_number, status=payload.status)
        db.add(row)
    else:
        row.status = payload.status
    row.completed_at = datetime.now(timezone.utc) if payload.status == "COMPLETED" else None
    db.commit()
    db.refresh(row)
    return row
