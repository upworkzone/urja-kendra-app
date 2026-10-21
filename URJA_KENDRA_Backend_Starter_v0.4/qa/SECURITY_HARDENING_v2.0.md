# Security Hardening v2.0

This milestone adds startup-time release configuration checks. It is a partial hardening step,
not a complete production security implementation.

## Added
- Production-like environments reject the known unsafe default JWT signing secret.
- Production-like environments reject secrets shorter than 32 characters.
- `OTP_PROVIDER=stub` is rejected outside local development.
- OTP TTL and maximum attempts configuration are bounded for non-development environments.
- CORS origins are exposed as a parsed property for explicit allow-list configuration.

## Still required before production
- Implement and verify a real SMS OTP provider.
- Add server-side request throttling by phone number and IP, abuse monitoring, and cooldowns.
- Verify OTP attempt counters are enforced atomically and challenge records are cleaned up.
- Use HTTPS at the ingress/reverse proxy and configure trusted proxy headers correctly.
- Use a managed secret store; rotate credentials and document rotation.
- Review CORS against actual clients and keep only exact allowed origins.
- Add migration tooling, backups, logging redaction, monitoring, privacy/retention rules, and security review.
- Run tests in a clean virtual environment and record actual output.

## Verification status
No test pass is claimed for this package. The previous report documented pytest collection blocked
by missing `python-jose` in the execution environment. Install `requirements.txt`, then rerun the
test suite and review any failures.
