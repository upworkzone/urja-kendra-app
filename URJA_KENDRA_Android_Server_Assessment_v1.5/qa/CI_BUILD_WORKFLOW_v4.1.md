# Android CI build workflow — v4.1

A GitHub Actions workflow is included at `.github/workflows/android-build.yml`.

It uses JDK 17 and Gradle 8.11.1, runs `assembleDebug` and `testDebugUnitTest`, then uploads the debug APK as a 14-day artifact.

## How to use

1. Extract the project ZIP and create/push it to a GitHub repository, preserving `.github/workflows/android-build.yml` at repository root.
2. Ensure the Android project folder is named `URJA_KENDRA_Android_Server_Assessment_v1.5` at repository root (as in this handoff).
3. Open the repository's **Actions** tab and inspect the **Android debug build** run.
4. Only download the APK artifact if both build and unit-test steps succeed.
5. Install it on an emulator/device and test OTP flow, catalogue, enrolment, lesson progress, assessment, certificate issue/download/open/share.

## Limits

- This workflow has been authored but not executed from this environment; it is not proof the project compiles.
- The current API base URL is the Android emulator's `10.0.2.2` development address. A physical-device test needs a reachable development server URL; production needs HTTPS and a controlled build configuration.
- This is a debug APK for private testing, not a signed release APK for Play Store distribution.
