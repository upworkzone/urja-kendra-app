from fastapi.testclient import TestClient
from app.main import app

def test_health():
    with TestClient(app) as client:
        r=client.get("/health")
        assert r.status_code==200 and r.json()["status"]=="ok"

def test_bilingual_catalogue():
    with TestClient(app) as client:
        en=client.get("/api/v1/courses?locale=en")
        hi=client.get("/api/v1/courses?locale=hi")
        assert en.status_code==200 and len(en.json())>=4
        assert hi.status_code==200 and any("सोलर" in c["title"] for c in hi.json())

def test_pathways():
    with TestClient(app) as client:
        r=client.get("/api/v1/career-pathways?locale=hi")
        assert r.status_code==200 and len(r.json())==4

def test_otp_development_flow():
    with TestClient(app) as client:
        req=client.post("/api/v1/auth/otp/request",json={"phone_number":"+919999999999","locale":"en"})
        assert req.status_code==202
        data=req.json()
        assert "challenge_id" in data and "development_only_code" in data
        verify=client.post("/api/v1/auth/otp/verify",json={"phone_number":"+919999999999","challenge_id":data["challenge_id"],"otp":data["development_only_code"]})
        assert verify.status_code==200
        token=verify.json()["access_token"]
        me=client.get("/api/v1/auth/me",headers={"Authorization":f"Bearer {token}"})
        assert me.status_code==200

def test_otp_preserves_hindi_locale_selected_at_request():
    """The locale selected before OTP delivery must survive verification."""
    with TestClient(app) as client:
        phone = "+919999999998"
        req = client.post("/api/v1/auth/otp/request", json={"phone_number": phone, "locale": "hi"})
        assert req.status_code == 202
        challenge = req.json()
        verify = client.post("/api/v1/auth/otp/verify", json={
            "phone_number": phone,
            "challenge_id": challenge["challenge_id"],
            "otp": challenge["development_only_code"],
        })
        assert verify.status_code == 200
        me = client.get("/api/v1/auth/me", headers={"Authorization": f"Bearer {verify.json()['access_token']}"})
        assert me.status_code == 200
        assert me.json()["preferred_locale"] == "hi"



def test_otp_challenge_cannot_be_replayed_after_successful_verification():
    """A successfully consumed OTP challenge must not mint a second token."""
    with TestClient(app) as client:
        phone = "+919999999997"
        request = client.post("/api/v1/auth/otp/request", json={"phone_number": phone, "locale": "en"})
        assert request.status_code == 202
        challenge = request.json()
        payload = {
            "phone_number": phone,
            "challenge_id": challenge["challenge_id"],
            "otp": challenge["development_only_code"],
        }
        first = client.post("/api/v1/auth/otp/verify", json=payload)
        assert first.status_code == 200
        replay = client.post("/api/v1/auth/otp/verify", json=payload)
        assert replay.status_code == 400
        assert replay.json()["detail"] == "Invalid or expired OTP"


def test_otp_challenge_is_bound_to_requested_phone_number():
    """A challenge cannot authenticate a different phone number."""
    with TestClient(app) as client:
        phone = "+919999999996"
        request = client.post("/api/v1/auth/otp/request", json={"phone_number": phone, "locale": "en"})
        assert request.status_code == 202
        challenge = request.json()
        wrong_phone = client.post("/api/v1/auth/otp/verify", json={
            "phone_number": "+919999999995",
            "challenge_id": challenge["challenge_id"],
            "otp": challenge["development_only_code"],
        })
        assert wrong_phone.status_code == 400
        # A mismatched phone attempt must not consume the legitimate challenge.
        correct_phone = client.post("/api/v1/auth/otp/verify", json={
            "phone_number": phone,
            "challenge_id": challenge["challenge_id"],
            "otp": challenge["development_only_code"],
        })
        assert correct_phone.status_code == 200


def test_otp_rejects_correct_code_after_maximum_failed_attempts():
    """Repeated wrong codes exhaust the challenge and prevent later success."""
    with TestClient(app) as client:
        phone = "+919999999994"
        request = client.post("/api/v1/auth/otp/request", json={"phone_number": phone, "locale": "en"})
        assert request.status_code == 202
        challenge = request.json()
        payload = {
            "phone_number": phone,
            "challenge_id": challenge["challenge_id"],
            "otp": "000000" if challenge["development_only_code"] != "000000" else "111111",
        }
        for _ in range(5):
            response = client.post("/api/v1/auth/otp/verify", json=payload)
            assert response.status_code == 400
        # Even the correct code must fail once the attempt budget is exhausted.
        payload["otp"] = challenge["development_only_code"]
        final = client.post("/api/v1/auth/otp/verify", json=payload)
        assert final.status_code == 400
        assert final.json()["detail"] == "Invalid or expired OTP"


def _development_access_token(client, phone_number):
    req = client.post("/api/v1/auth/otp/request", json={"phone_number": phone_number, "locale": "en"})
    assert req.status_code == 202
    challenge = req.json()
    verify = client.post("/api/v1/auth/otp/verify", json={
        "phone_number": phone_number,
        "challenge_id": challenge["challenge_id"],
        "otp": challenge["development_only_code"],
    })
    assert verify.status_code == 200
    return verify.json()["access_token"]


def test_assessment_rejects_invalid_option_and_accepts_valid_submission():
    with TestClient(app) as client:
        token = _development_access_token(client, "+919999999991")
        headers = {"Authorization": f"Bearer {token}"}
        enrolled = client.post("/api/v1/courses/ev-01/enroll", headers=headers)
        assert enrolled.status_code == 201

        assessment = client.get("/api/v1/courses/ev-01/assessment?locale=en", headers=headers)
        assert assessment.status_code == 200
        questions = assessment.json()["questions"]
        answers = [
            {"question_id": question["id"], "option_id": question["options"][0]["id"]}
            for question in questions
        ]

        invalid = [dict(answer) for answer in answers]
        invalid[0]["option_id"] = "not-an-option"
        rejected = client.post("/api/v1/courses/ev-01/assessment/attempts",
                              headers=headers, json={"answers": invalid})
        assert rejected.status_code == 422

        duplicate = [dict(answer) for answer in answers]
        duplicate[-1]["question_id"] = duplicate[0]["question_id"]
        rejected_duplicate = client.post("/api/v1/courses/ev-01/assessment/attempts",
                                         headers=headers, json={"answers": duplicate})
        assert rejected_duplicate.status_code == 422

        accepted = client.post("/api/v1/courses/ev-01/assessment/attempts",
                               headers=headers, json={"answers": answers})
        assert accepted.status_code == 201
        result = accepted.json()
        assert result["question_count"] == len(questions)
        assert 0 <= result["score_percent"] <= 100


def test_certificate_lifecycle_guardrails_before_eligibility():
    """Learners must not issue/download a certificate before meeting requirements."""
    with TestClient(app) as client:
        token = _development_access_token(client, "+919999999992")
        headers = {"Authorization": f"Bearer {token}"}
        enrolled = client.post("/api/v1/courses/ev-01/enroll", headers=headers)
        assert enrolled.status_code == 201

        eligibility = client.get(
            "/api/v1/me/courses/ev-01/certificate-eligibility", headers=headers
        )
        assert eligibility.status_code == 200
        state = eligibility.json()
        assert state["eligible_for_certificate"] is False
        assert state["certificate_issued"] is False
        assert state["certificate_number"] is None

        issue = client.post("/api/v1/me/courses/ev-01/certificate", headers=headers)
        assert issue.status_code == 409
        assert "pass" in issue.json()["detail"].lower()

        pdf = client.get("/api/v1/me/courses/ev-01/certificate.pdf", headers=headers)
        assert pdf.status_code == 404

        unknown = client.get("/api/v1/certificates/UK-NOTREAL12345/verify")
        assert unknown.status_code == 200
        assert unknown.json()["valid"] is False


def test_certificate_routes_require_authentication_for_learner_data():
    with TestClient(app) as client:
        issue = client.post("/api/v1/me/courses/ev-01/certificate")
        pdf = client.get("/api/v1/me/courses/ev-01/certificate.pdf")
        eligibility = client.get("/api/v1/me/courses/ev-01/certificate-eligibility")
        assert issue.status_code == 401
        assert pdf.status_code == 401
        assert eligibility.status_code == 401


def test_certificate_issue_is_idempotent_and_pdf_is_verifiable():
    """Exercise issuance, repeat issuance, PDF download, and public verification."""
    from app.db.session import SessionLocal
    from app.models.user import User
    from app.models.assessment import AssessmentAttempt
    import uuid

    phone = "+919999999993"
    with TestClient(app) as client:
        token = _development_access_token(client, phone)
        headers = {"Authorization": f"Bearer {token}"}
        enrolled = client.post("/api/v1/courses/ev-01/enroll", headers=headers)
        assert enrolled.status_code == 201

        # Mark all eight EV-01 modules complete through the public progress API.
        for module_number in range(1, 9):
            response = client.put("/api/v1/courses/ev-01/progress", headers=headers,
                                  json={"module_number": module_number, "status": "COMPLETED"})
            assert response.status_code == 200

        # Seed a passing assessment attempt as test setup; assessment scoring is
        # covered separately by test_assessment_rejects_invalid_option_and_accepts_valid_submission.
        with SessionLocal() as db:
            user = db.query(User).filter_by(phone_number=phone).one()
            db.add(AssessmentAttempt(
                id=str(uuid.uuid4()), user_id=user.id, course_id="ev-01",
                score_percent=90, correct_count=9, question_count=10, passed=True,
                threshold_percent=70, answers_json={"test_fixture": True},
            ))
            db.commit()

        first = client.post("/api/v1/me/courses/ev-01/certificate", headers=headers)
        assert first.status_code == 201, first.text
        certificate_number = first.json()["certificate_number"]
        assert first.json()["issued"] is True

        second = client.post("/api/v1/me/courses/ev-01/certificate", headers=headers)
        assert second.status_code == 201, second.text
        assert second.json()["certificate_number"] == certificate_number

        eligibility = client.get("/api/v1/me/courses/ev-01/certificate-eligibility", headers=headers)
        assert eligibility.status_code == 200
        assert eligibility.json()["certificate_issued"] is True
        assert eligibility.json()["certificate_number"] == certificate_number

        pdf = client.get("/api/v1/me/courses/ev-01/certificate.pdf", headers=headers)
        assert pdf.status_code == 200
        assert pdf.headers["content-type"].startswith("application/pdf")
        assert pdf.content.startswith(b"%PDF-")

        verification = client.get(f"/api/v1/certificates/{certificate_number}/verify")
        assert verification.status_code == 200
        assert verification.json()["valid"] is True
        assert verification.json()["certificate_number"] == certificate_number


def test_otp_request_rate_limit_returns_429_without_blocking_other_numbers():
    """The fourth request in the configured window is throttled per phone number."""
    with TestClient(app) as client:
        phone = "+919888777666"
        statuses = []
        for _ in range(4):
            response = client.post(
                "/api/v1/auth/otp/request",
                json={"phone_number": phone, "locale": "en"},
            )
            statuses.append(response.status_code)

        assert statuses == [202, 202, 202, 429]
        assert "too many otp requests" in response.json()["detail"].lower()

        # Throttling is scoped to the phone number, not a global lockout.
        other = client.post(
            "/api/v1/auth/otp/request",
            json={"phone_number": "+919888777667", "locale": "en"},
        )
        assert other.status_code == 202
