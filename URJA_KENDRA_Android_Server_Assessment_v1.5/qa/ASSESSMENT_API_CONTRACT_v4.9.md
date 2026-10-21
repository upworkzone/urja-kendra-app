# Assessment API contract alignment — v4.9

## Defect fixed

The backend's assessment endpoint returns locale-specific `question` and option `text` fields. The Android DTO previously expected only `en`/`hi`, which meant the UI could render blank question and option labels even when the API returned valid assessment content.

## Changes

- `AssessmentQuestionDto` accepts the current `question` field plus optional bilingual `en`/`hi` fields.
- `AssessmentOptionDto` accepts the current `text` field plus optional bilingual `en`/`hi` fields.
- UI label helpers prefer the locale-specific backend fields and retain bilingual fallback support.
- Added a unit test using the actual backend response shape.
- Added `qa/check_assessment_api_contract.py` to compare the source contract.

## Verification status

Run `python qa/check_assessment_api_contract.py` from the Android project directory. This is a source-contract check, not a substitute for Gradle compilation or an end-to-end API test. Android build/test execution remains pending until the GitHub workflow runs.
