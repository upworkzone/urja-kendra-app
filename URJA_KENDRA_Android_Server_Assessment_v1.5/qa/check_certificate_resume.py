from pathlib import Path

source = Path(__file__).parents[1] / "app/src/main/java/in/urjakendra/app/ui/ServerAssessmentScreen.kt"
text = source.read_text(encoding="utf-8")
checks = {
    "eligibility is reloaded when the screen opens": "repository.getCertificateEligibility(courseId, token)" in text,
    "eligibility refresh does not block assessment on failure": "Keep assessment usable if certificate status is unavailable." in text,
    "returning learner state is shown before another attempt": "if (result == null)" in text and "Certificate has already been issued." in text,
    "returning learner can download an issued certificate": '"Download PDF"' in text and "pendingCertificateDownload = PendingCertificateDownload(courseId, token)" in text,
    "stale claim that issuance is unavailable was removed": "certificate issuance is not yet available" not in text,
    "Hindi issued-state label exists": "प्रमाणपत्र जारी हो चुका है।" in text,
}
for label, ok in checks.items():
    print(f"{'PASS' if ok else 'FAIL'}: {label}")
if not all(checks.values()):
    raise SystemExit(1)
print(f"Certificate resume source checks passed ({len(checks)} assertions).")
