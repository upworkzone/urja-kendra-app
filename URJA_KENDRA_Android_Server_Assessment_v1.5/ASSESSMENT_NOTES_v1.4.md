# URJA KENDRA v1.4 — EV-01 Practice Assessment

## Included
- Bilingual English/Hindi EV-01 practice assessment with 8 multiple-choice questions.
- Requires all questions to be answered before submission.
- Calculates score locally and uses a configurable 70% practice threshold.
- Shows answer feedback and supports retry.
- Accessible from the EV-01 lesson/progress screen.

## Important limitations
- This is a client-side practice assessment only. The score and pass status are not sent to or verified by the backend.
- It does not grant a certificate, prove identity, or qualify a learner for high-voltage EV work.
- Do not use this client-only threshold for paid course completion, employment decisions, or certificate issuance.
- Before a production assessment, implement server-side question delivery/scoring, attempt limits, persisted results, versioned question banks, audit logging, secure authentication, and an admin-configurable threshold. Define accommodations, retake policy, question review, and course-specific passing criteria.
- Content is an introductory draft and requires review by qualified EV/safety subject-matter experts.

## Build status
Source-only milestone. A Gradle build and end-to-end API test were not run in this environment. Please import the project into Android Studio and build/test before distribution.
