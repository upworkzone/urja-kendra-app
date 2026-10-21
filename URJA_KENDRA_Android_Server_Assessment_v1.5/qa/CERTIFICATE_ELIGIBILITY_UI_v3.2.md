# Certificate eligibility UI integration v3.2

After a successful server-scored assessment, the Android screen now calls the
existing certificate-eligibility endpoint and displays the eligibility result.
The UI explicitly distinguishes eligibility from actual certificate issuance.

Limitations:
- The backend exposes eligibility, not certificate file generation/issuance.
- The UI has not been compiled or exercised against a running API in this environment.
- If the eligibility call fails after a successful assessment, the UI shows an error;
  the submitted assessment may already have been saved server-side.
