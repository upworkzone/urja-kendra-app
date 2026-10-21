from io import BytesIO
from uuid import uuid4
from datetime import datetime, timezone
from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import StreamingResponse
from sqlalchemy.orm import Session
from reportlab.lib.pagesizes import landscape, A4
from reportlab.lib import colors
from reportlab.pdfgen import canvas

from app.api.deps import current_user
from app.db.session import get_db
from app.models.user import User
from app.models.course import Course
from app.models.learning import Enrollment, LessonProgress
from app.models.assessment import AssessmentAttempt
from app.models.certificate import Certificate

router = APIRouter(prefix="/api/v1", tags=["certificates"])

def _eligibility(db: Session, user: User, course: Course):
    completed = db.query(LessonProgress).filter_by(
        user_id=user.id, course_id=course.id, status="COMPLETED"
    ).count()
    latest = db.query(AssessmentAttempt).filter_by(
        user_id=user.id, course_id=course.id
    ).order_by(AssessmentAttempt.submitted_at.desc()).first()
    return completed >= course.module_count and bool(latest and latest.passed)

@router.post("/me/courses/{course_id}/certificate", status_code=201)
def issue_certificate(course_id: str, user: User = Depends(current_user), db: Session = Depends(get_db)):
    course = db.get(Course, course_id)
    if not course or not course.published:
        raise HTTPException(status_code=404, detail="Course not found")
    if not db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first():
        raise HTTPException(status_code=403, detail="Enroll in this course first")
    if not _eligibility(db, user, course):
        raise HTTPException(status_code=409, detail="Complete all modules and pass the latest assessment first")
    existing = db.query(Certificate).filter_by(user_id=user.id, course_id=course_id).first()
    if existing:
        return {"id": existing.id, "certificate_number": existing.certificate_number,
                "course_id": course_id, "issued": True, "download_path": f"/api/v1/me/courses/{course_id}/certificate.pdf"}
    cert = Certificate(
        id=str(uuid4()), user_id=user.id, course_id=course_id,
        certificate_number="UK-" + uuid4().hex[:12].upper()
    )
    db.add(cert)
    db.commit()
    db.refresh(cert)
    return {"id": cert.id, "certificate_number": cert.certificate_number,
            "course_id": course_id, "issued": True, "download_path": f"/api/v1/me/courses/{course_id}/certificate.pdf"}

@router.get("/me/courses/{course_id}/certificate.pdf")
def download_certificate(course_id: str, user: User = Depends(current_user), db: Session = Depends(get_db)):
    cert = db.query(Certificate).filter_by(user_id=user.id, course_id=course_id).first()
    if not cert:
        raise HTTPException(status_code=404, detail="Certificate has not been issued")
    course = db.get(Course, course_id)
    if not course:
        raise HTTPException(status_code=404, detail="Course not found")
    # Use a masked phone number as the learner identifier until a verified name field exists.
    phone = user.phone_number or ""
    learner = ("Learner " + ("••••" + phone[-4:] if len(phone) >= 4 else ""))
    buffer = BytesIO()
    page = landscape(A4)
    pdf = canvas.Canvas(buffer, pagesize=page)
    width, height = page
    pdf.setFillColor(colors.HexColor("#FFFDF9")); pdf.rect(0, 0, width, height, fill=1, stroke=0)
    pdf.setStrokeColor(colors.HexColor("#2E9D50")); pdf.setLineWidth(4)
    pdf.rect(24, 24, width - 48, height - 48, fill=0, stroke=1)
    pdf.setFillColor(colors.HexColor("#F36B21")); pdf.setFont("Helvetica-Bold", 18)
    pdf.drawCentredString(width / 2, height - 76, "URJA KENDRA")
    pdf.setFillColor(colors.HexColor("#1D252B")); pdf.setFont("Helvetica-Bold", 28)
    pdf.drawCentredString(width / 2, height - 128, "CERTIFICATE OF COMPLETION")
    pdf.setFont("Helvetica", 14); pdf.drawCentredString(width / 2, height - 172, "This certifies that")
    pdf.setFont("Helvetica-Bold", 22); pdf.drawCentredString(width / 2, height - 208, learner)
    pdf.setFont("Helvetica", 14); pdf.drawCentredString(width / 2, height - 246, "has completed the learning requirements for")
    pdf.setFont("Helvetica-Bold", 20); pdf.drawCentredString(width / 2, height - 280, course.title_en)
    pdf.setFont("Helvetica", 10)
    pdf.drawCentredString(width / 2, 78, f"Certificate No: {cert.certificate_number}")
    issued = cert.issued_at or datetime.now(timezone.utc)
    pdf.drawCentredString(width / 2, 60, f"Issue date: {issued.strftime('%Y-%m-%d')} | Verify: /api/v1/certificates/{cert.certificate_number}/verify")
    pdf.setFont("Helvetica-Oblique", 9)
    pdf.drawCentredString(width / 2, 42, "Training completion record. Not a government licence or professional authorization.")
    pdf.showPage(); pdf.save(); buffer.seek(0)
    headers = {"Content-Disposition": f'attachment; filename="URJA-KENDRA-{cert.certificate_number}.pdf"'}
    return StreamingResponse(buffer, media_type="application/pdf", headers=headers)

@router.get("/certificates/{certificate_number}/verify")
def verify_certificate(certificate_number: str, db: Session = Depends(get_db)):
    cert = db.query(Certificate).filter_by(certificate_number=certificate_number).first()
    if not cert:
        return {"valid": False, "certificate_number": certificate_number}
    course = db.get(Course, cert.course_id)
    return {"valid": True, "certificate_number": cert.certificate_number,
            "course_id": cert.course_id, "course_title": course.title_en if course else cert.course_id,
            "issued_at": cert.issued_at.isoformat() if cert.issued_at else None}
