# Android build check v3.0

## Current source status
The Android manifest and all project XML files parse successfully. A real Gradle
build has not been run in this environment. The project currently does not include
`gradlew` / `gradlew.bat`, and no system Gradle executable is available here, so a
build cannot be honestly reported as successful.

## Run a build
1. Extract the project.
2. Open `URJA_KENDRA_Android_Server_Assessment_v1.5` in Android Studio.
3. Install/select JDK 17 and an Android SDK matching `compileSdk = 35`.
4. Let Gradle sync and resolve dependencies.
5. Run `Build > Make Project`, then `Build > Build Bundle(s) / APK(s) > Build APK(s)`.

You can also run:
- macOS/Linux: `qa/build_android.sh`
- Windows PowerShell: `qa/build_android.ps1`

These scripts check Java and use the Gradle wrapper if present, otherwise use
system Gradle if installed. They stop with a clear blocker message if neither is
available. They do not download or fabricate a wrapper.

## Expected debug APK path
After a successful build:
`app/build/outputs/apk/debug/app-debug.apk`

## Before production
Change the development API URL (`http://10.0.2.2:8000/`) to a deployed HTTPS URL,
disable cleartext traffic, and validate authentication, course flows, and privacy
settings on physical devices.
