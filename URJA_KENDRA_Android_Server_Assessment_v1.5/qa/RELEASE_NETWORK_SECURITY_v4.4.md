# Release network security gate v4.4

The emulator-only URL is configured for the `debug` build type. The `release` build type requires `-PURJA_API_BASE_URL=https://<real-host>/`; it rejects missing, non-HTTPS, local, or placeholder values before `preReleaseBuild` proceeds. The release manifest disables cleartext HTTP, while debug permits it for the local emulator backend.

Before production: deploy a real HTTPS API, verify TLS, configure OTP delivery and abuse controls, run backend migrations, review privacy and certificate wording, then build and test the release variant. This guard is not a substitute for a security review.
