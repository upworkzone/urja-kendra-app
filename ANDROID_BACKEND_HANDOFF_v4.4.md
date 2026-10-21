# URJA KENDRA Developer Handoff v4.4

## Milestone: prevent accidental insecure release builds

- Debug builds keep the Android emulator URL `http://10.0.2.2:8000/` and permit cleartext traffic for local development only.
- Release builds read the API base URL from the Gradle property `URJA_API_BASE_URL`.
- Release manifest configuration disables cleartext traffic.
- The `preReleaseBuild` task depends on `verifyReleaseConfig`, which blocks release builds unless the configured URL is HTTPS, ends in `/`, and is not a localhost/emulator or placeholder URL.

## Build commands

Debug APK (local backend on Android emulator):

```bash
gradle --no-daemon assembleDebug
```

Release build (only after a real production API host is deployed):

```bash
gradle --no-daemon -PURJA_API_BASE_URL=https://api.example.invalid/ assembleRelease
```

Replace the example URL with the actual HTTPS API host. The placeholder above is intentionally rejected by the guard; it is not a usable production endpoint. Never put secrets in this property; it is a public app URL, not a credential.

## Verification status

- Source-level assertions verify debug/release URL separation, cleartext configuration and release guard presence.
- Android manifest XML parsing is checked locally.
- This environment does not have the Android SDK/Gradle installed, so no Gradle build or APK installation is claimed as verified.
- Backend OTP stub, database migration, real OTP delivery, production deployment, privacy/legal review, and end-to-end device tests remain release blockers.
