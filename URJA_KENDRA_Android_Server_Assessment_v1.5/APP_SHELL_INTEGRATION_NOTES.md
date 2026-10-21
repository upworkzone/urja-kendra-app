# v0.9 — App shell authentication integration

This package starts from the consolidated v0.8 source and adds `AuthHost.kt`, then changes `MainActivity` to show the host.

Expected behavior:
- On launch, if a token exists, call the authenticated profile endpoint.
- If the profile call succeeds, open the existing `UrjaApp` shell.
- If no token or validation fails, clear invalid token and show mobile/OTP login.
- After OTP verification, `AuthScreen` calls `onAuthenticated` and opens the shell.

## Caveats
- Source prepared; no Gradle build or emulator test has been completed.
- Login ViewModel and repository signatures are aligned to the current local source, but the backend must be running and its API responses must match the DTOs.
- This does not yet replace sample data in Courses or My Learning.
- The base URL uses cleartext HTTP for local emulator development. Do not ship this configuration. Production must use HTTPS and a real SMS OTP provider.
- Test session expiry, logout, network errors, Android process death, and the full auth → enrolment → progress journey before private testing.
