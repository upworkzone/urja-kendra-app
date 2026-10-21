from pathlib import Path

workflow = Path(__file__).resolve().parents[2] / ".github" / "workflows" / "android-build.yml"
text = workflow.read_text(encoding="utf-8")
required = [
    "actions/setup-java@v4",
    "android-actions/setup-android@v3",
    'sdkmanager "platforms;android-35" "build-tools;35.0.0"',
    "gradle-version: '8.11.1'",
    "assembleDebug",
    "testDebugUnitTest",
    "lintDebug",
    "urja-kendra-android-lint-report",
    "urja-kendra-debug-apk",
]
missing = [item for item in required if item not in text]
if missing:
    raise SystemExit("Missing Android CI workflow configuration: " + ", ".join(missing))
print(f"Android CI workflow contract passed ({len(required)} assertions).")
