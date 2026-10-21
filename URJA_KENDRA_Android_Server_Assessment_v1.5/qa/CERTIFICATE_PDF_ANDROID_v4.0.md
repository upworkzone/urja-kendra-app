# Certificate PDF Android integration v4.0

## Implemented in source
- Retrofit streaming GET for `/api/v1/me/courses/{courseId}/certificate.pdf`.
- Repository wrapper with authenticated bearer token.
- PDF response saved under app-private `files/certificates/` storage.
- `FileProvider` grants a read-only content URI for open/share intents; no broad storage permission is requested.
- Bilingual Download PDF, Open PDF and Share PDF actions appear after the server reports an issued certificate.
- Eligibility DTO reads the optional `certificate_number` returned by the backend, so previously issued certificates can show their number after reloading eligibility.

## Manual verification still required
1. Run backend and sign in with a test account.
2. Enrol in a published course, complete every module, pass assessment, and issue certificate.
3. Download the PDF; confirm it opens and is a valid PDF.
4. Open and share the file on Android 10+ and Android 7–9.
5. Confirm an unissued certificate cannot be downloaded, and a failed network request produces an error.
6. Verify privacy: files are app-private; share/open uses a temporary read grant through FileProvider.

## Limitations
- Build and device verification have not been performed in this environment.
- App-private storage means users access the file through the in-app Open/Share actions rather than a public Downloads folder.
- The API returns the PDF only after issuance; the backend must be running and reachable.
