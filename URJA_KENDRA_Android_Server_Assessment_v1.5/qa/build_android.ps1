$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")
Write-Host "URJA KENDRA Android build check"
Write-Host "Project: $(Get-Location)"
try { java -version } catch {
    Write-Error "Java not found. Install JDK 17 and configure JAVA_HOME."
    exit 2
}
if (Test-Path ".\gradlew.bat") {
    .\gradlew.bat --no-daemon assembleDebug
    exit $LASTEXITCODE
}
Write-Host "BLOCKED: Gradle wrapper (gradlew.bat) is missing."
Write-Host "Open project in Android Studio, allow Gradle sync, and add/generate the Gradle wrapper."
$gradle = Get-Command gradle -ErrorAction SilentlyContinue
if ($gradle) {
    gradle assembleDebug
    exit $LASTEXITCODE
}
exit 3
