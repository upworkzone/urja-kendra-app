# Certificate issuance backend v3.3

Implemented:
- Certificate records with a unique user/course constraint.
- Authenticated, idempotent issuance endpoint requiring enrolment, completion of all
  course modules, and a passing latest assessment.
- PDF download endpoint.
- Public verification endpoint by certificate number.
- PDF dependency declared in requirements.

Important limitations:
- PDF currently identifies the learner by a masked phone number because no verified
  learner-name field exists. It uses English text only.
- Issuer wording and credential policy are provisional and require business/legal
  approval before release.
- The certificate explicitly says it is not a government licence or professional
  authorization.
- Full pytest/runtime, PDF rendering, migrations on existing deployed databases,
  and Android integration have not been run here. The backend uses create_all for
  local development; production schema changes need a migration strategy.
