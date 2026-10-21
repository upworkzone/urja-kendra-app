# URJA KENDRA Developer Handoff v4.7

## Milestone
Replaced the placeholder Android unit test with API-model parsing tests for the live course DTO, certificate eligibility states, and server-authored assessment payloads.

## Added checks
- Verifies Gson maps snake_case API fields to Kotlin properties.
- Verifies an already-issued certificate number is parsed.
- Verifies a not-yet-issued certificate can have a null certificate number.
- Verifies assessment question/option payload parsing.

## Verification status
- Tests are included in the Android Gradle `testDebugUnitTest` task already present in GitHub Actions.
- ZIP integrity can be checked locally.
- Android Gradle compilation and tests have not been run in this environment because Gradle and the Android SDK are unavailable here. Do not treat these tests as passed until GitHub Actions runs successfully.
- No APK is included or claimed to be verified.

## Next action
Push the project to GitHub and run `Android debug build`. If the new tests or compilation fail, use the `urja-kendra-build-diagnostics` artifact to identify and fix the actual error.
