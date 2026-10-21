from pydantic import BaseModel, ConfigDict
class CourseOut(BaseModel):
    id: str
    code: str
    category: str
    title: str
    summary: str
    module_count: int
    published: bool
    model_config = ConfigDict(from_attributes=True)
