# URJA KENDRA Backend v0.4 — assessment attempts

## Added
- Authenticated `GET /api/v1/courses/{course_id}/assessment?locale=en|hi` returns the EV-01 question set without answer keys.
- Authenticated `POST /api/v1/courses/{course_id}/assessment/attempts` validates exactly one answer per question, scores on the server, and persists attempt history.
- `GET /api/v1/me/courses/{course_id}/certificate-eligibility` checks completion of all course modules plus latest passing attempt. It never issues a certificate.
- `assessment_attempts` database table stores score, pass result, threshold, answers, and submission time.

## Pilot rules
- EV-01 only; other seeded courses intentionally return 404 for assessment until reviewed content exists.
- Pass threshold is 70%, configured in `app/api/assessment_routes.py`.
- This is a development starter. Existing OTP is a development stub, default secrets are unsafe, and database `create_all` is not a production migration strategy. Before real users: configure HTTPS and secret management, real SMS OTP, throttling, audit/privacy policies, migrations, backups, monitoring, and security review.
- Assessment questions and Hindi translations require qualified instructional and technical review. Passing this quiz does not certify practical high-voltage competence.

## Run locally
From this folder: `pip install -r requirements.txt`, configure `.env` from `.env.example`, then `uvicorn app.main:app --reload`. Use `/docs` to explore endpoints. The Android emulator base URL is typically `http://10.0.2.2:8000/`; use HTTPS and a real API hostname for any deployment.
