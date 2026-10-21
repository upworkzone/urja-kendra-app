#!/usr/bin/env python3
"""End-to-end smoke test for local URJA KENDRA backend. Uses development OTP stub only."""
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request

BASE = os.environ.get("URJA_API_BASE_URL", "http://127.0.0.1:8000").rstrip("/")
PHONE = os.environ.get("URJA_TEST_PHONE", "+919999999999")
COURSE_ID = "ev-01"
# Developer-only fixture: never returned by the public assessment endpoint.
EXPECTED_ANSWERS = {
    "evq1": "a", "evq2": "a", "evq3": "b", "evq4": "a",
    "evq5": "c", "evq6": "a", "evq7": "b", "evq8": "c",
}
passed = 0
failed = 0

def call(method, path, payload=None, token=None, expected=(200,)):
    url = BASE + path
    body = None if payload is None else json.dumps(payload).encode()
    headers = {"Accept": "application/json"}
    if body is not None:
        headers["Content-Type"] = "application/json"
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            status = response.status
            raw = response.read().decode()
    except urllib.error.HTTPError as exc:
        status = exc.code
        raw = exc.read().decode()
    except Exception as exc:
        raise RuntimeError(f"{method} {path}: request failed: {exc}") from exc
    if status not in expected:
        raise AssertionError(f"{method} {path}: expected {expected}, got {status}: {raw[:400]}")
    try:
        data = json.loads(raw) if raw else {}
    except json.JSONDecodeError:
        data = {}
    return data

def check(label, fn):
    global passed, failed
    try:
        result = fn()
        passed += 1
        print(f"PASS  {label}")
        return result
    except Exception as exc:
        failed += 1
        print(f"FAIL  {label}: {exc}")
        return None

def main():
    health = check("health endpoint", lambda: call("GET", "/health"))
    if health is None:
        print("Backend is not reachable; start it before running this script.")
        return 2
    check("English catalogue", lambda: (
        len(call("GET", "/api/v1/courses?locale=en")) >= 4 or
        (_ for _ in ()).throw(AssertionError("expected at least 4 courses"))
    ))
    check("Hindi career pathways", lambda: (
        len(call("GET", "/api/v1/career-pathways?locale=hi")) == 4 or
        (_ for _ in ()).throw(AssertionError("expected 4 pathways"))
    ))

    def auth():
        req = call("POST", "/api/v1/auth/otp/request",
                   {"phone_number": PHONE, "locale": "en"}, expected=(202,))
        challenge = req.get("challenge_id")
        otp = req.get("development_only_code")
        if not challenge or not otp:
            raise AssertionError("development OTP stub did not return challenge/code; this script is local-dev only")
        result = call("POST", "/api/v1/auth/otp/verify",
                      {"phone_number": PHONE, "challenge_id": challenge, "otp": otp})
        token = result.get("access_token")
        if not token:
            raise AssertionError("access token missing")
        return token
    token = check("development OTP login", auth)
    if not token:
        print(f"\nSummary: {passed} passed, {failed} failed")
        return 1

    check("authenticated profile", lambda: call("GET", "/api/v1/auth/me", token=token))

    def enroll():
        result = call("POST", f"/api/v1/courses/{COURSE_ID}/enroll?locale=en", token=token, expected=(201,))
        if result.get("course_id") != COURSE_ID:
            raise AssertionError(f"unexpected course ID: {result}")
        return result
    check("EV-01 enrolment", enroll)
    check("enrolment listed in My Learning", lambda: (
        any(x.get("course_id") == COURSE_ID for x in call("GET", "/api/v1/me/enrollments", token=token))
        or (_ for _ in ()).throw(AssertionError("EV-01 enrolment not found"))
    ))

    def progress():
        for module in range(1, 9):
            call("PUT", f"/api/v1/courses/{COURSE_ID}/progress",
                 {"module_number": module, "status": "COMPLETED"}, token=token)
        rows = call("GET", f"/api/v1/courses/{COURSE_ID}/progress", token=token)
        completed = sum(1 for row in rows if row.get("status") == "COMPLETED")
        if completed < 8:
            raise AssertionError(f"expected 8 completed modules, got {completed}")
    check("save and reload all 8 lesson modules", progress)

    def assessment():
        data = call("GET", f"/api/v1/courses/{COURSE_ID}/assessment?locale=en", token=token)
        questions = data.get("questions", [])
        if len(questions) != len(EXPECTED_ANSWERS):
            raise AssertionError(f"expected 8 questions, got {len(questions)}")
        if any("key" in q or "answer" in q for q in questions):
            raise AssertionError("public assessment response appears to expose answer keys")
        answers = [{"question_id": q["id"], "option_id": EXPECTED_ANSWERS[q["id"]]} for q in questions]
        result = call("POST", f"/api/v1/courses/{COURSE_ID}/assessment/attempts",
                      {"answers": answers}, token=token, expected=(201,))
        if not result.get("passed") or result.get("score_percent", 0) < result.get("threshold_percent", 70):
            raise AssertionError(f"known passing fixture did not pass: {result}")
        return result
    check("server-side assessment scoring and saved attempt response", assessment)

    def eligibility():
        result = call("GET", f"/api/v1/me/courses/{COURSE_ID}/certificate-eligibility", token=token)
        if not result.get("eligible_for_certificate"):
            raise AssertionError(f"expected eligibility after completed modules and passing attempt: {result}")
        if result.get("certificate_issued") is not False:
            raise AssertionError("eligibility endpoint must not claim a certificate was issued")
    check("certificate eligibility (without issuing certificate)", eligibility)

    print(f"\nSummary: {passed} passed, {failed} failed")
    print("This script uses a development OTP stub and modifies local database records.")
    return 0 if failed == 0 else 1

if __name__ == "__main__":
    try:
        sys.exit(main())
    except RuntimeError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        sys.exit(2)
