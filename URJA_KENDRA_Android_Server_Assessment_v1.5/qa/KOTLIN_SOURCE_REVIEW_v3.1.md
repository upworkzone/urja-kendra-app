# Kotlin source review v3.1

## Corrected source issue
`ServerAssessmentScreen.kt` referenced `question.question` and `option.text`, but
the corresponding DTOs expose localized `en` / `hi` fields. Updated the screen to
use the existing `questionTitle(question, isHindi)` and
`optionLabel(option, isHindi)` helpers.

## Verification boundary
- Confirmed the two invalid property references no longer appear in the screen.
- Android XML and API contract source checks are separate and remain available.
- Kotlin compilation has not been run: this environment lacks Gradle and a Gradle
  wrapper, so this is a source review, not proof the app compiles.
