# URJA KENDRA Developer Handoff v4.2

## Milestone
Improved Android CI failure diagnostics.

## Changes
- Gradle build and unit-test commands now include `--stacktrace` for more actionable logs.
- GitHub Actions uploads available Gradle reports and test results on failure.
- Updated CI instructions clarify where to find build artifacts and diagnostics.

## Verification boundary
The workflow YAML was inspected as text and source package integrity is checked, but GitHub Actions has not been run here. No successful Android build or APK is claimed.
