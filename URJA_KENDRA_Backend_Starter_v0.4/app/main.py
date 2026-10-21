from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.db.session import Base, engine, SessionLocal
from app.models.course import Course
from app.models.user import User
from app.models.otp_challenge import OtpChallenge
from app.models.learning import Enrollment, LessonProgress
from app.models.assessment import AssessmentAttempt
from app.models.certificate import Certificate
from app.services.seed import seed_courses
from app.api.auth_routes import router as auth_router
from app.api.course_routes import router as course_router
from app.api.profile_routes import router as profile_router
from app.api.career_routes import router as career_router
from app.api.learning_routes import router as learning_router
from app.api.assessment_routes import router as assessment_router
from app.api.certificate_routes import router as certificate_router

@asynccontextmanager
async def lifespan(app: FastAPI):
    # FastAPI lifespan handlers replace legacy startup/shutdown event handlers.
    # Validate before touching the database so unsafe release configuration fails closed.
    settings.validate_release_security()
    Base.metadata.create_all(bind=engine)
    with SessionLocal() as db:
        seed_courses(db)
    yield

app = FastAPI(title=settings.app_name, version="0.4.1", lifespan=lifespan)

app.add_middleware(CORSMiddleware,
    allow_origins=[o.strip() for o in settings.cors_origins.split(",") if o.strip()],
    allow_credentials=True, allow_methods=["GET","POST","PUT","DELETE","OPTIONS"],
    allow_headers=["Authorization","Content-Type"])
@app.get("/health")
def health():
    return {"status":"ok","environment":settings.environment}
app.include_router(auth_router)
app.include_router(course_router)
app.include_router(profile_router)
app.include_router(career_router)
app.include_router(learning_router)
app.include_router(assessment_router)
app.include_router(certificate_router)
