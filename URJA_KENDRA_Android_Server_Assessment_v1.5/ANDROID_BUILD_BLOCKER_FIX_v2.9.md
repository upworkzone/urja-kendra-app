# Android manifest correction v2.9

## Finding
The AndroidManifest.xml contained literal `\\n` text sequences in XML markup
instead of actual newline characters. That makes the manifest malformed XML and
is a direct Android build blocker.

## Fix
Replaced those literal sequences with actual line breaks.

## Verification
- AndroidManifest.xml parses as XML.
- All Android XML files in the project parse successfully.
- This does **not** prove the Kotlin source compiles or the Gradle build succeeds.
  A full Android build still needs to run in Android Studio or an environment with
  the Android SDK and Gradle dependencies installed.

## Development network configuration
The manifest currently allows cleartext HTTP for local emulator development and
the base URL points to `10.0.2.2`. Before production release, use HTTPS and a
production API URL; do not ship the development configuration unchanged.
