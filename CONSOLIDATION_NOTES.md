# Consolidated Android source v0.8

This package starts from the actual Fresh Project source and adds authentication source that matches its existing API repository signatures:
- `requestOtp(phone, locale)` returns an OTP response containing `challengeId`.
- `verifyOtp(phone, challengeId, otp)` returns a token response.
- `SessionStore` saves the token using Android encrypted preferences.

## Important
The new `AuthScreen` is not yet wired into `MainActivity` navigation. Do not assume the full app now uses live authentication or live course data. This package is a consolidated source baseline plus compatible auth/session files, not a release APK.

## Build
The project currently has no Gradle wrapper and this environment does not have Gradle installed, so a Gradle build could not be run here. Open the folder in Android Studio, sync Gradle, allow it to generate/use a compatible wrapper, then run `assembleDebug`.

## Next integration steps
1. Wire `AuthScreen` into `MainActivity` with an `AuthViewModel` factory.
2. Route to the existing app shell on `Authenticated`, and persist session on launch.
3. Replace sample catalogue and learning screens with repository data.
4. Verify backend DTOs/routes and run end-to-end tests.
5. Before release, remove cleartext HTTP; use HTTPS and a real OTP provider. The manifest currently permits cleartext for local development only.
