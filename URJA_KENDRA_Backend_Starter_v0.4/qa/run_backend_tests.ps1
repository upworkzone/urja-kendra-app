$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")
Write-Host "URJA KENDRA backend test runner"
python --version
$modules = @("fastapi", "sqlalchemy", "pydantic_settings", "psycopg", "jose", "passlib", "reportlab", "pytest", "httpx")
$missing = @()
foreach ($module in $modules) {
    python -c "import $module" 2>$null
    if ($LASTEXITCODE -ne 0) { $missing += $module }
}
if ($missing.Count -gt 0) {
    Write-Error ("Missing Python modules: " + ($missing -join ", ") + ". From this backend directory run: python -m pip install -r requirements.txt. If package-index access is unavailable, use an approved local wheelhouse.")
    exit 2
}
python -m compileall -q app tests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python -m pytest -q tests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Backend tests passed."
