package `in`.urjakendra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import `in`.urjakendra.app.ui.LiveCoursesScreen

private val UrjaOrange = Color(0xFFF36B21)
private val UrjaGreen = Color(0xFF2E9D50)
private val UrjaInk = Color(0xFF1D252B)
private val UrjaCream = Color(0xFFFFFDF9)

data class Course(val code: String, val titleEn: String, val titleHi: String, val category: String, val lessons: Int, val progress: Int = 0, val backendId: String = code)
data class Career(val titleEn: String, val titleHi: String, val descEn: String, val descHi: String)

private val sampleCourses = listOf(
    Course("SOL-01", "Solar PV Fundamentals", "सोलर पीवी की मूल बातें", "Solar", 8, 25),
    Course("EV-01", "EV Fundamentals", "ईवी की मूल बातें", "EV", 8, 60),
    Course("BAT-01", "Battery Fundamentals", "बैटरी की मूल बातें", "Batteries", 8, 0),
    Course("ECS-01", "EV Charging Fundamentals", "ईवी चार्जिंग की मूल बातें", "EV Charging", 8, 0)
)
private val sampleCareers = listOf(
    Career("Dealer / Franchise", "डीलर / फ्रेंचाइज़ी", "Explore starting a clean-energy business.", "स्वच्छ ऊर्जा व्यवसाय शुरू करने के विकल्प जानें।"),
    Career("Installer / Technician", "इंस्टॉलर / तकनीशियन", "Explore supervised technical career pathways.", "तकनीकी करियर विकल्पों की जानकारी लें।"),
    Career("Distributor / Retail Partner", "डिस्ट्रिब्यूटर / रिटेल पार्टनर", "Explore product distribution and retail.", "उत्पाद वितरण और रिटेल के विकल्प जानें।"),
    Career("Third-Party Employer Referral", "थर्ड-पार्टी नियोक्ता रेफरल", "Express interest in employer opportunities.", "नियोक्ता अवसरों में रुचि दर्ज करें।")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { `in`.urjakendra.app.ui.AuthHost { UrjaApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrjaApp() {
    var isHindi by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var selectedLearningCourseId by remember { mutableStateOf<String?>(null) }
    var selectedCareer by remember { mutableStateOf<Career?>(null) }
    var enrolMessage by remember { mutableStateOf<String?>(null) }
    val appContext = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { `in`.urjakendra.app.data.UrjaRepository() }
    val sessionStore = remember { `in`.urjakendra.app.data.SessionStore(appContext) }
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = UrjaOrange,
            secondary = UrjaGreen,
            background = UrjaCream,
            surface = Color.White,
            onPrimary = Color.White,
            onSurface = UrjaInk
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = UrjaCream) {
            Scaffold(
                containerColor = UrjaCream,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = R.drawable.urja_logo_source),
                                    contentDescription = "URJA KENDRA logo",
                                    modifier = Modifier.width(104.dp).height(38.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("URJA KENDRA", fontWeight = FontWeight.ExtraBold, color = UrjaInk, fontSize = 16.sp)
