from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.models.course import Course
from app.schemas.course import CourseOut

router = APIRouter(prefix="/api/v1", tags=["courses"])

def localized(row: Course, locale: str) -> CourseOut:
    return CourseOut(id=row.id, code=row.code, category=row.category,
        title=row.title_hi if locale == "hi" else row.title_en,
        summary=row.summary_hi if locale == "hi" else row.summary_en,
        module_count=row.module_count, published=row.published)

@router.get("/courses", response_model=list[CourseOut])
def list_courses(locale: str = Query("en", pattern="^(en|hi)$"),
                 category: str | None = Query(None, pattern="^(SOLAR|EV|BATTERIES|EV_CHARGING)$"),
                 db: Session = Depends(get_db)):
    query = db.query(Course).filter(Course.published.is_(True))
    if category:
        query = query.filter(Course.category == category)
    return [localized(row, locale) for row in query.order_by(Course.code).all()]

@router.get("/courses/{course_id}", response_model=CourseOut)
def course_detail(course_id: str, locale: str = Query("en", pattern="^(en|hi)$"), db: Session = Depends(get_db)):
    row = db.get(Course, course_id)
    if not row or not row.published:
        raise HTTPException(status_code=404, detail="Course not found")
    return localized(row, locale)
