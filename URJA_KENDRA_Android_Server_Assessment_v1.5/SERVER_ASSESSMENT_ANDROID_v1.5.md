# URJA KENDRA Android Server Assessment Integration v1.5

## Added
- Retrofit declarations for `GET /api/v1/courses/{courseId}/assessment`, `POST /api/v1/courses/{courseId}/assessment/attempts`, and `GET /api/v1/me/courses/{courseId}/certificate-eligibility`.
- DTOs matching backend v0.4 localized assessment response (question + text fields), server-scored attempt response, and eligibility response.
- Repository wrappers that attach the bearer token.
- `ServerAssessmentScreen.kt`: fetches server questions, requires one answer per question, submits answer IDs to server, and displays the returned score/pass result. It does not calculate the score locally and does not issue certificates.

## Integration status
This is an integration component, not yet connected to the existing `LiveLessonScreen` navigation. Wire `ServerAssessmentScreen(courseId, isHindi, repository, sessionStore, onBack)` into the lesson flow after enrollment. Use backend course ID `ev-01` for the pilot. The API base URL must point to a running backend.

## Important contract note
Backend v0.4 returns localized question data as `question` and option `text`; this package's DTOs match that shape. The client never receives answer keys. Server-side scoring remains authoritative.

## Not verified
No Gradle wrapper/installed Gradle is available in the creation environment; this source has not been compiled or tested end-to-end. Validate DTOs against the running backend, connect navigation, and run Android tests before distribution. Backend OTP remains development-only and must not be used with real users.
