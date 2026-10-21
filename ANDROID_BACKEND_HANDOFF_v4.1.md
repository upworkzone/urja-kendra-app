# URJA KENDRA Developer Handoff v4.1

## Milestone
Added a GitHub Actions Android build workflow so the source can be compiled in a clean Linux environment with JDK 17 and Gradle 8.11.1. It runs `assembleDebug`, runs unit tests, and uploads the debug APK only when those steps succeed.

## Why
The current authoring environment does not have Gradle/Android SDK configured, so a local APK could not be verified. CI provides a repeatable next step rather than claiming a build that has not happened.

## Status
- Workflow source included: yes
- Workflow executed: no
- APK generated and verified: no
- Device/emulator test: no
- Production configuration/security review: pending

See `URJA_KENDRA_Android_Server_Assessment_v1.5/qa/CI_BUILD_WORKFLOW_v4.1.md` for usage and limitations.
