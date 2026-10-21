# URJA KENDRA Android API integration — v0.3

This increment adds Retrofit DTOs, API interface and repository methods for the FastAPI v0.3 backend. The existing Compose screens remain sample-driven; this is a service layer, not a claim that the UI is already connected.

## Configure API URL

`app/build.gradle.kts` defines `URJA_API_BASE_URL` as a build config field.
- Android Emulator talking to a backend running on the same development computer: `http://10.0.2.2:8000/`
- Physical device: use the development computer's reachable LAN IP and ensure firewall/network access.
- Production: use your deployed HTTPS API URL. Do not ship a public build with cleartext HTTP.

The base URL must end in `/`.

## Run the backend

Follow the backend v0.3 README. Ensure `/health` returns `{"status":"ok"}`. The development OTP stub is for local testing only and must never be used with real users.

## Example use from a ViewModel coroutine

```kotlin
viewModelScope.launch {
    try {
        val courses = repository.getCourses(locale = "hi")
        // Publish courses to UI state on the main coroutine.
    } catch (e: retrofit2.HttpException) {
        // Handle HTTP errors, including 401 by returning to sign-in.
    } catch (e: java.io.IOException) {
        // Show a network-unavailable state.
    }
}
```

## Important integration details

- Authenticate with `requestOtp`, then `verifyOtp`; use the returned `accessToken` for protected methods.
- Access tokens expire. Handle `401` gracefully and require re-authentication until refresh-token support is implemented.
- Do not persist tokens in plain preferences. Implement Android Keystore-backed encrypted storage before a real-device test.
- Do not show `developmentOnlyCode` in a production UI; the backend only emits it in development stub mode.
- Backend course IDs are currently seeded identifiers such as `sol-01`, `ev-01`, `bat-01`, and `ecs-01`.
- The current backend tracks module status, not actual lesson content or assessment results. Certificate issuance is not implemented.
- No build or end-to-end API test has been claimed for this integration pack. Verify in Android Studio and test against a running backend before release.
