from datetime import datetime
from pydantic import BaseModel, Field, ConfigDict

class EnrollmentOut(BaseModel):
    id: str
    course_id: str
    course_code: str
    course_title: str
    status: str
    enrolled_at: datetime
    completed_modules: int
    total_modules: int
    progress_percent: int
    model_config = ConfigDict(from_attributes=True)

class ProgressIn(BaseModel):
    module_number: int = Field(ge=1)
    status: str = Field(pattern="^(IN_PROGRESS|COMPLETED)$")

class ProgressOut(BaseModel):
    module_number: int
    status: str
    completed_at: datetime | None
    updated_at: datetime
    model_config = ConfigDict(from_attributes=True)
