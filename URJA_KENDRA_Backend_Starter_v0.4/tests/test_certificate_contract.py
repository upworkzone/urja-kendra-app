"""Source contract checks for certificate issue, download and verification."""
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]

def test_certificate_router_registered():
    source = (ROOT / "app" / "main.py").read_text(encoding="utf-8")
    assert "certificate_router" in source
    assert "app.include_router(certificate_router)" in source

def test_certificate_issue_checks_learning_eligibility_and_is_idempotent():
    source = (ROOT / "app" / "api" / "certificate_routes.py").read_text(encoding="utf-8")
    assert '"/me/courses/{course_id}/certificate"' in source
    assert "_eligibility(db, user, course)" in source
    assert "uq_certificate_user_course" in (ROOT / "app" / "models" / "certificate.py").read_text(encoding="utf-8")
    assert "existing = db.query(Certificate)" in source

def test_pdf_and_public_verification_routes_exist():
    source = (ROOT / "app" / "api" / "certificate_routes.py").read_text(encoding="utf-8")
    assert '"/me/courses/{course_id}/certificate.pdf"' in source
    assert '"/certificates/{certificate_number}/verify"' in source
    assert 'media_type="application/pdf"' in source


def test_eligibility_endpoint_reflects_issued_certificate():
    source = (ROOT / "app" / "api" / "assessment_routes.py").read_text(encoding="utf-8")
    assert "from app.models.certificate import Certificate" in source
    assert "db.query(Certificate).filter_by(user_id=user.id, course_id=course_id).first()" in source
    assert '"certificate_issued":issued' in source
    assert '"certificate_number":certificate.certificate_number if certificate else None' in source
    assert "Certificate already issued." in source
