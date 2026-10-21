#!/usr/bin/env python3
"""Source contract checks for assessment submission validation (no app deps required)."""
from pathlib import Path
import ast

ROOT = Path(__file__).resolve().parents[1]
route = ROOT / "app/api/assessment_routes.py"
source = route.read_text(encoding="utf-8")
ast.parse(source)
checks = {
    "duplicate question IDs rejected": "Duplicate question IDs are not allowed" in source,
    "exact question set required": "Submit exactly one answer for each assessment question" in source,
    "allowed option IDs derived from server question bank": 'valid_options = {q["id"]: {option["id"] for option in q["options"]} for q in QUESTIONS}' in source,
    "invalid question-option combinations rejected": "One or more option IDs are invalid for their questions" in source,
    "score remains server calculated": 'submitted[q["id"]] == q["key"]' in source,
    "answer key not included in localized GET response": '"key":q["key"]' not in source and '"key": q["key"]' not in source,
}
for label, passed in checks.items():
    print(("PASS" if passed else "FAIL") + " - " + label)
if not all(checks.values()):
    raise SystemExit(1)
print(f"{len(checks)} assessment submission contract checks passed")
