from uuid import uuid4
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.api.deps import current_user
from app.db.session import get_db
from app.models.user import User
from app.models.course import Course
from app.models.learning import Enrollment, LessonProgress
from app.models.assessment import AssessmentAttempt
from app.models.certificate import Certificate
from app.schemas.assessment import AttemptIn, AttemptOut

router = APIRouter(prefix="/api/v1", tags=["assessment"])
PASS_THRESHOLD = 70

# Pilot assessment content; answer keys are deliberately never returned by GET.
QUESTIONS = [
 {"id":"evq1","en":"Which component stores energy in most EVs?","hi":"अधिकांश ईवी में ऊर्जा किस घटक में संग्रहित होती है?","options":[{"id":"a","en":"Traction battery","hi":"ट्रैक्शन बैटरी"},{"id":"b","en":"Radiator","hi":"रेडिएटर"},{"id":"c","en":"Windscreen","hi":"विंडस्क्रीन"}],"key":"a"},
 {"id":"evq2","en":"What does regenerative braking do?","hi":"रीजेनेरेटिव ब्रेकिंग क्या करती है?","options":[{"id":"a","en":"Converts some motion energy back to electrical energy","hi":"गति ऊर्जा के कुछ भाग को विद्युत ऊर्जा में बदलती है"},{"id":"b","en":"Guarantees zero stopping distance","hi":"रुकने की दूरी शून्य कर देती है"},{"id":"c","en":"Replaces all routine maintenance","hi":"सारा नियमित रखरखाव समाप्त कर देती है"}],"key":"a"},
 {"id":"evq3","en":"Before charging, what should a learner do?","hi":"चार्जिंग से पहले शिक्षार्थी को क्या करना चाहिए?","options":[{"id":"a","en":"Ignore cable damage","hi":"केबल की क्षति को अनदेखा करें"},{"id":"b","en":"Check equipment and follow the manufacturer instructions","hi":"उपकरण जाँचें और निर्माता के निर्देश मानें"},{"id":"c","en":"Use any improvised connector","hi":"कोई भी अस्थायी कनेक्टर इस्तेमाल करें"}],"key":"b"},
 {"id":"evq4","en":"Which factor can affect EV range?","hi":"ईवी की रेंज पर कौन-सा कारक असर डाल सकता है?","options":[{"id":"a","en":"Driving speed and temperature","hi":"ड्राइविंग गति और तापमान"},{"id":"b","en":"Vehicle paint name only","hi":"केवल वाहन के रंग का नाम"},{"id":"c","en":"Number of dashboard icons","hi":"डैशबोर्ड आइकन की संख्या"}],"key":"a"},
 {"id":"evq5","en":"What should you do if a high-voltage component is damaged?","hi":"यदि हाई-वोल्टेज घटक क्षतिग्रस्त हो तो क्या करें?","options":[{"id":"a","en":"Touch it to check temperature","hi":"तापमान जाँचने के लिए छुएँ"},{"id":"b","en":"Keep operating the vehicle","hi":"वाहन चलाते रहें"},{"id":"c","en":"Keep clear and contact trained, authorised personnel","hi":"दूर रहें और प्रशिक्षित, अधिकृत कर्मियों से संपर्क करें"}],"key":"c"},
 {"id":"evq6","en":"What is the role of an inverter in many EV powertrains?","hi":"कई ईवी पावरट्रेन में इन्वर्टर की भूमिका क्या है?","options":[{"id":"a","en":"Converts electrical power between battery and motor requirements","hi":"बैटरी और मोटर की आवश्यकताओं के अनुसार विद्युत शक्ति बदलता है"},{"id":"b","en":"Inflates tyres","hi":"टायर में हवा भरता है"},{"id":"c","en":"Measures cabin colour","hi":"केबिन का रंग मापता है"}],"key":"a"},
 {"id":"evq7","en":"Why use only compatible charging equipment?","hi":"केवल अनुकूल चार्जिंग उपकरण क्यों उपयोग करें?","options":[{"id":"a","en":"To bypass all protections","hi":"सभी सुरक्षा उपाय हटाने के लिए"},{"id":"b","en":"To support safe operation and correct ratings","hi":"सुरक्षित संचालन और सही रेटिंग सुनिश्चित करने के लिए"},{"id":"c","en":"It always doubles range","hi":"यह हमेशा रेंज दोगुनी करता है"}],"key":"b"},
 {"id":"evq8","en":"Who should perform specialist high-voltage repairs?","hi":"विशेषज्ञ हाई-वोल्टेज मरम्मत किसे करनी चाहिए?","options":[{"id":"a","en":"Any new learner","hi":"कोई भी नया शिक्षार्थी"},{"id":"b","en":"Untrained bystanders","hi":"बिना प्रशिक्षण वाले दर्शक"},{"id":"c","en":"Qualified, authorised technicians using approved procedures","hi":"स्वीकृत प्रक्रियाओं का पालन करने वाले योग्य, अधिकृत तकनीशियन"}],"key":"c"},
]

def localized_questions(locale: str):
    if locale not in ("en", "hi"):
        raise HTTPException(status_code=422, detail="locale must be en or hi")
    return [{"id":q["id"],"question":q[locale],"options":[{"id":o["id"],"text":o[locale]} for o in q["options"]]} for q in QUESTIONS]

@router.get("/courses/{course_id}/assessment")
def get_assessment(course_id: str, locale: str = "en", user: User = Depends(current_user), db: Session = Depends(get_db)):
    course = db.get(Course, course_id)
    if not course or not course.published:
        raise HTTPException(status_code=404, detail="Course not found")
    if not db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first():
        raise HTTPException(status_code=403, detail="Enroll in this course first")
    if course_id != "ev-01":
        raise HTTPException(status_code=404, detail="Assessment not yet available for this course")
    return {"course_id":course_id,"title":"EV-01 Assessment" if locale == "en" else "EV-01 मूल्यांकन","threshold_percent":PASS_THRESHOLD,"questions":localized_questions(locale)}

@router.post("/courses/{course_id}/assessment/attempts", response_model=AttemptOut, status_code=201)
def submit_attempt(course_id: str, payload: AttemptIn, user: User = Depends(current_user), db: Session = Depends(get_db)):
    course = db.get(Course, course_id)
    if not course or not course.published:
        raise HTTPException(status_code=404, detail="Course not found")
    if not db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first():
        raise HTTPException(status_code=403, detail="Enroll in this course first")
    if course_id != "ev-01":
        raise HTTPException(status_code=404, detail="Assessment not yet available for this course")
    submitted = {a.question_id:a.option_id for a in payload.answers}
    valid_ids = {q["id"] for q in QUESTIONS}
    if len(submitted) != len(payload.answers):
        raise HTTPException(status_code=422, detail="Duplicate question IDs are not allowed")
    if set(submitted) != valid_ids:
        raise HTTPException(status_code=422, detail="Submit exactly one answer for each assessment question")
    # Reject option IDs that do not belong to the corresponding question. Without
    # this check, arbitrary option IDs would silently be scored as incorrect.
    valid_options = {q["id"]: {option["id"] for option in q["options"]} for q in QUESTIONS}
    invalid_answers = [question_id for question_id, option_id in submitted.items()
                       if option_id not in valid_options[question_id]]
    if invalid_answers:
        raise HTTPException(status_code=422, detail="One or more option IDs are invalid for their questions")
    correct = sum(submitted[q["id"]] == q["key"] for q in QUESTIONS)
    count = len(QUESTIONS)
    score = round(correct * 100 / count)
    attempt = AssessmentAttempt(id=str(uuid4()), user_id=user.id, course_id=course_id, score_percent=score,
        correct_count=correct, question_count=count, passed=score >= PASS_THRESHOLD,
        threshold_percent=PASS_THRESHOLD, answers_json=submitted)
    db.add(attempt); db.commit(); db.refresh(attempt)
    return attempt

@router.get("/me/courses/{course_id}/certificate-eligibility")
def certificate_eligibility(course_id: str, user: User = Depends(current_user), db: Session = Depends(get_db)):
    course = db.get(Course, course_id)
    if not course or not course.published:
        raise HTTPException(status_code=404, detail="Course not found")
    if not db.query(Enrollment).filter_by(user_id=user.id, course_id=course_id).first():
        raise HTTPException(status_code=403, detail="Enroll in this course first")
    completed = db.query(LessonProgress).filter_by(user_id=user.id, course_id=course_id, status="COMPLETED").count()
    latest = db.query(AssessmentAttempt).filter_by(user_id=user.id, course_id=course_id).order_by(AssessmentAttempt.submitted_at.desc()).first()
    lessons_done = completed >= course.module_count
    assessment_passed = bool(latest and latest.passed)
    eligible = lessons_done and assessment_passed
    certificate = db.query(Certificate).filter_by(user_id=user.id, course_id=course_id).first()
    issued = certificate is not None
    return {"course_id":course_id,"completed_modules":completed,"total_modules":course.module_count,
        "latest_assessment_passed":assessment_passed,"eligible_for_certificate":eligible,
        "certificate_issued":issued,
        "certificate_number":certificate.certificate_number if certificate else None,
        "note":("Certificate already issued." if issued else
                "Ready to issue certificate." if eligible else
                "Complete all modules and pass the latest assessment to qualify.")}
