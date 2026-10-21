"""Verify the extracted handoff has workflow and project paths expected by CI."""
from pathlib import Path
import sys

root = Path(__file__).resolve().parent
checks = {
    "Android workflow exists": root / ".github/workflows/android-build.yml",
    "Backend workflow exists": root / ".github/workflows/backend-tests.yml",
    "Android project exists": root / "URJA_KENDRA_Android_Server_Assessment_v1.5/settings.gradle.kts",
    "Backend project exists": root / "URJA_KENDRA_Backend_Starter_v0.4/app/main.py",
}
failed = False
for label, path in checks.items():
    ok = path.is_file()
    print(f"{'PASS' if ok else 'FAIL'}: {label} ({path.relative_to(root)})")
    failed |= not ok
if failed:
    print("Copy the contents of the handoff folder into the GitHub repository root.")
    sys.exit(1)
print("Repository-root layout is consistent with workflow paths.")
