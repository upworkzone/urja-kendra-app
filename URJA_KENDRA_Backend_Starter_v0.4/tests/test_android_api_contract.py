"""Source-level contract checks for Android Retrofit paths vs backend routes."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT.parent / "URJA_KENDRA_Android_Server_Assessment_v1.5" / "app" / "src" / "main" / "java" / "in" / "urjakendra" / "app" / "data"

def read_android_api():
    return (ANDROID / "UrjaApi.kt").read_text(encoding="utf-8")

def read_backend_routes():
    api_dir = ROOT / "app" / "api"
    return "\n".join(p.read_text(encoding="utf-8") for p in api_dir.glob("*.py"))

def test_android_uses_backend_profile_and_auth_paths():
    android = read_android_api()
    backend = read_backend_routes()
    assert '@GET("api/v1/me")' in android
    assert '@router.get("/me"' in backend
    assert '@GET("health")' in android
    assert '@app.get("/health")' in (ROOT / "app" / "main.py").read_text(encoding="utf-8")

def test_android_learning_and_assessment_paths_exist_in_backend():
    android = read_android_api()
    backend = read_backend_routes()
    for route in [
        "api/v1/courses/{courseId}/enroll",
        "api/v1/me/enrollments",
        "api/v1/courses/{courseId}/progress",
        "api/v1/courses/{courseId}/assessment",
        "api/v1/courses/{courseId}/assessment/attempts",
        "api/v1/me/courses/{courseId}/certificate-eligibility",
    ]:
        assert route in android, f"Android path missing: {route}"
    for route in [
        '"/courses/{course_id}/enroll"',
        '"/me/enrollments"',
        '"/courses/{course_id}/progress"',
        '"/courses/{course_id}/assessment"',
        '"/courses/{course_id}/assessment/attempts"',
        '"/me/courses/{course_id}/certificate-eligibility"',
    ]:
        assert route in backend, f"Backend route missing: {route}"

def test_bilingual_catalogue_and_pathway_paths_exist():
    android = read_android_api()
    backend = read_backend_routes()
    assert '@GET("api/v1/courses")' in android and '"/courses"' in backend
    assert '@GET("api/v1/career-pathways")' in android and '"/career-pathways"' in backend
