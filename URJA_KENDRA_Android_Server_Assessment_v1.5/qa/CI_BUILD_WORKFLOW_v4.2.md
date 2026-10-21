# Android CI build workflow — v4.2

The GitHub Actions workflow is at `.github/workflows/android-build.yml`. It sets up JDK 17 and Gradle 8.11.1, runs `assembleDebug` and `testDebugUnitTest` with stack traces, uploads the APK only when the job reaches the artifact step, and uploads available Gradle reports/test results if a step fails.

## Run it

1. Extract this ZIP and push the entire folder to a GitHub repository. The `.github/workflows` directory must be at the repository root.
2. Keep `URJA_KENDRA_Android_Server_Assessment_v1.5` at the repository root.
3. Open **Actions → Android debug build** and inspect the run.
4. On success, download `urja-kendra-debug-apk`. On failure, download `urja-kendra-build-diagnostics` if present and inspect the first Gradle/Kotlin error in the run logs.
5. Install the debug APK and test OTP, catalogue, enrolment, lesson progress, assessment, and certificate issue/download/open/share.

## Boundaries

- Workflow changes improve diagnostics; they do not prove the build succeeds. This workflow has not been executed in this environment.
- The emulator API base URL `10.0.2.2` works for Android emulators only. Physical devices need a reachable server address; production must use HTTPS.
- This is a private-test debug APK workflow, not a signed Play Store release workflow.
