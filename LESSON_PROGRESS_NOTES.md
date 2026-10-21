# v1.2 — Lesson progress persistence

This source patch adds a module progress screen backed by the existing progress API:
- `GET /api/v1/courses/{courseId}/progress`
- `PUT /api/v1/courses/{courseId}/progress`

Learners can mark modules 1–8 complete/incomplete; the UI reloads the server state after each update. The My Learning action now routes using the backend course ID rather than trying to match sample course codes.

## Important limitations
- These are progress controls, not actual course lesson content. The module range is currently fixed at 8 and should be replaced with the course's actual module metadata.
- Assessment, pass threshold, certificate generation and verification are not implemented.
- Backend API behavior and authorization must be tested. This package is not built or emulator-tested.
- Do not treat module checkbox completion as proof of learning or as the final certificate assessment.
