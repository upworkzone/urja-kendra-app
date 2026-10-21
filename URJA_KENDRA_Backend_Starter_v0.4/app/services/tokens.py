from datetime import datetime, timedelta, timezone
from jose import jwt
from app.core.config import settings

ALGORITHM = "HS256"

def create_access_token(user_id: str) -> str:
    now = datetime.now(timezone.utc)
    payload = {"sub": user_id, "iat": int(now.timestamp()), "exp": int((now + timedelta(minutes=settings.access_token_minutes)).timestamp())}
    return jwt.encode(payload, settings.auth_secret_key, algorithm=ALGORITHM)
