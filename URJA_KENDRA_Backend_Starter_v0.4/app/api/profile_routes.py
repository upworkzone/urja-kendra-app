from fastapi import APIRouter, Depends
from app.api.deps import current_user
from app.models.user import User
from app.schemas.auth import UserOut

router = APIRouter(prefix="/api/v1", tags=["profile"])

@router.get("/me", response_model=UserOut)
def get_me(user: User = Depends(current_user)):
    return UserOut(id=user.id, phone_number=user.phone_number, preferred_locale=user.preferred_locale)
