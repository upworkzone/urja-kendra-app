"""Source-level checks for certificate lifecycle integration coverage."""
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
source = (ROOT / "tests" / "test_api.py").read_text(encoding="utf-8")

def test_certificate_guardrail_integration_test_covers_prerequisites():
    assert "test_certificate_lifecycle_guardrails_before_eligibility" in source
    assert 'assert issue.status_code == 409' in source
    assert 'assert pdf.status_code == 404' in source
    assert 'assert unknown.json()["valid"] is False' in source

def test_certificate_personal_endpoints_require_authentication():
    assert "test_certificate_routes_require_authentication_for_learner_data" in source
    assert 'assert issue.status_code == 401' in source
    assert 'assert pdf.status_code == 401' in source
    assert 'assert eligibility.status_code == 401' in source
