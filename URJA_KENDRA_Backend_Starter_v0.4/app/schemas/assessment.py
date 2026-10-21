from pydantic import BaseModel, Field
from datetime import datetime

class AnswerIn(BaseModel):
    question_id: str = Field(min_length=1, max_length=40)
    option_id: str = Field(min_length=1, max_length=40)

class AttemptIn(BaseModel):
    answers: list[AnswerIn] = Field(min_length=1, max_length=30)

class AttemptOut(BaseModel):
    id: str
    course_id: str
    score_percent: int
    correct_count: int
    question_count: int
    passed: bool
    threshold_percent: int
    submitted_at: datetime
