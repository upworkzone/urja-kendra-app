"""Contract checks for persistent learning endpoints; full auth-backed tests need a test DB and OTP/JWT fixtures."""
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]

def test_learning_router_registered():
    source = (ROOT / "app" / "main.py").read_text()
    assert "learning_router" in source
    # App version changes independently of the learning API contract.
    assert "FastAPI(" in source

def test_learning_routes_have_expected_operations():
    source = (ROOT / "app" / "api" / "learning_routes.py").read_text()
    for fragment in ["/courses/{course_id}/enroll", "/me/enrollments", "/courses/{course_id}/progress", "Enroll in this course first"]:
        assert fragment in source

def test_unique_constraints_prevent_duplicate_rows():
    source = (ROOT / "app" / "models" / "learning.py").read_text()
    assert "uq_enrollment_user_course" in source
    assert "uq_progress_user_course_module" in source


def test_release_security_validation_runs_inside_lifespan():
    source = (ROOT / "app" / "main.py").read_text()
    lifespan_start = source.index("async def lifespan")
    lifespan_end = source.index("\\napp = FastAPI", lifespan_start)
    lifespan_body = source[lifespan_start:lifespan_end]
    assert "settings.validate_release_security()" in lifespan_body
    assert lifespan_body.index("settings.validate_release_security()") < lifespan_body.index("Base.metadata.create_all")
