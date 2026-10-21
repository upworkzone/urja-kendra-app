from sqlalchemy.orm import Session
from app.models.course import Course
COURSES = [
 dict(id="sol-01",code="SOL-01",category="SOLAR",title_en="Solar PV Fundamentals",title_hi="सोलर पीवी की मूल बातें",summary_en="Learn core concepts of solar photovoltaic systems.",summary_hi="सौर फोटोवोल्टिक प्रणालियों की मूल अवधारणाएँ सीखें।",module_count=8,published=True),
 dict(id="ev-01",code="EV-01",category="EV",title_en="EV Fundamentals",title_hi="ईवी की मूल बातें",summary_en="Understand electric vehicle systems and safe-use basics.",summary_hi="इलेक्ट्रिक वाहन प्रणालियों और सुरक्षित उपयोग की मूल बातें समझें।",module_count=8,published=True),
 dict(id="bat-01",code="BAT-01",category="BATTERIES",title_en="Battery Fundamentals",title_hi="बैटरी की मूल बातें",summary_en="Explore battery types, operation, and safe handling.",summary_hi="बैटरी के प्रकार, संचालन और सुरक्षित रखरखाव जानें।",module_count=8,published=True),
 dict(id="ecs-01",code="ECS-01",category="EV_CHARGING",title_en="EV Charging Fundamentals",title_hi="ईवी चार्जिंग की मूल बातें",summary_en="Learn AC/DC charging concepts and site basics.",summary_hi="AC/DC चार्जिंग और चार्जिंग स्थल की मूल बातें सीखें।",module_count=8,published=True),
]
def seed_courses(db: Session):
    for row in COURSES:
        if db.get(Course,row["id"]) is None:
            db.add(Course(**row))
    db.commit()
