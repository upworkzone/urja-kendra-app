from fastapi import APIRouter, Query

router = APIRouter(prefix="/api/v1", tags=["careers"])
PATHWAYS = [
("DEALER_FRANCHISE","Dealer / Franchise","डीलर / फ्रेंचाइज़ी","Explore clean-energy business opportunities. Approval is not guaranteed.","स्वच्छ ऊर्जा व्यवसाय के अवसर जानें। स्वीकृति की गारंटी नहीं है।"),
("INSTALLER_TECHNICIAN","Installer / Technician","इंस्टॉलर / तकनीशियन","Explore technical learning and supervised work pathways.","तकनीकी प्रशिक्षण और पर्यवेक्षित कार्य के विकल्प जानें।"),
("DISTRIBUTOR_RETAIL","Distributor / Retail Partner","डिस्ट्रिब्यूटर / रिटेल पार्टनर","Explore distribution and retail partner opportunities.","वितरण और रिटेल पार्टनर के अवसर जानें।"),
("EMPLOYER_REFERRAL","Third-Party Employer Referral","थर्ड-पार्टी नियोक्ता रेफरल","Express interest in potential employer referrals. Employment is not guaranteed.","संभावित नियोक्ता रेफरल में रुचि दर्ज करें। नौकरी की गारंटी नहीं है।")
]
@router.get("/career-pathways")
def career_pathways(locale: str = Query("en", pattern="^(en|hi)$")):
    return [{"code":c, "title":hi if locale=="hi" else en, "description":dhi if locale=="hi" else den}
            for c,en,hi,den,dhi in PATHWAYS]
