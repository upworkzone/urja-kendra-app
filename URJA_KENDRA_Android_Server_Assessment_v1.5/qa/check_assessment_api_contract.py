from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
models = (ROOT / "app/src/main/java/in/urjakendra/app/data/ApiModels.kt").read_text()
screen = (ROOT / "app/src/main/java/in/urjakendra/app/ui/ServerAssessmentScreen.kt").read_text()
backend = (ROOT.parent / "URJA_KENDRA_Backend_Starter_v0.4/app/api/assessment_routes.py")
if not backend.exists():
    # The handoff root keeps the backend beside the Android project.
    backend = ROOT.parent / "URJA_KENDRA_Backend_Starter_v0.4/app/api/assessment_routes.py"
backend_text = backend.read_text() if backend.exists() else ""
checks = {
    "Android model supports backend question field": "val question: String? = null" in models,
    "Android model supports backend option text field": "val text: String? = null" in models,
    "UI prefers server-localized question": "q.question?.takeIf { it.isNotBlank() }" in screen,
    "UI prefers server-localized option text": "o.text?.takeIf { it.isNotBlank() }" in screen,
    "Backend returns localized question field": '"question":q[locale]' in backend_text,
    "Backend returns localized option text field": '"text":o[locale]' in backend_text,
}
for label, ok in checks.items():
    print(f"{'PASS' if ok else 'FAIL'}: {label}")
if not all(checks.values()):
    raise SystemExit(1)
