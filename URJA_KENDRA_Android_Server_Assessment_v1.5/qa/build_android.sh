#!/usr/bin/env bash
set -u
cd "$(dirname "$0")/.."
echo "URJA KENDRA Android build check"
echo "Project: $(pwd)"
if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: Java not found. Install JDK 17 and configure JAVA_HOME."
  exit 2
fi
java -version
if [ ! -x "./gradlew" ]; then
  echo "BLOCKED: Gradle wrapper (./gradlew) is missing or not executable."
  echo "Open this project in Android Studio, allow Gradle sync, then generate/add the Gradle wrapper."
  echo "Alternatively, if Gradle is installed system-wide, run: gradle assembleDebug"
  if command -v gradle >/dev/null 2>&1; then
    echo "System Gradle found; running assembleDebug..."
    gradle assembleDebug
    exit $?
  fi
  exit 3
fi
./gradlew --no-daemon assembleDebug
