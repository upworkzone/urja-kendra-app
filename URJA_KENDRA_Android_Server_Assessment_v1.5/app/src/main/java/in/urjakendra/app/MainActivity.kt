package in.urjakendra.app

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
        setContent { in.urjakendra.app.ui.AuthHost { UrjaApp() } }
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
    val repository = remember { in.urjakendra.app.data.UrjaRepository() }
    val sessionStore = remember { in.urjakendra.app.data.SessionStore(appContext) }
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
                                    Text("ऊर्जा बनाओ, ऊर्जा बचाओ, पैसे कमाओ...", color = UrjaGreen, fontSize = 9.sp, maxLines = 1)
                                }
                            }
                        },
                        actions = {
                            TextButton(onClick = { isHindi = !isHindi }) {
                                Text(if (isHindi) "EN" else "हिं", color = UrjaOrange, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        val labels = if (isHindi) listOf("होम", "कोर्स", "मेरी पढ़ाई", "करियर", "प्रोफ़ाइल")
                                     else listOf("Home", "Courses", "My Learning", "Careers", "Profile")
                        val icons = listOf(Icons.Default.Home, Icons.Default.MenuBook, Icons.Default.School, Icons.Default.Work, Icons.Default.Person)
                        labels.forEachIndexed { index, label ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index; selectedCourse = null; selectedCareer = null; selectedLearningCourseId = null },
                                icon = { Icon(icons[index], contentDescription = label) },
                                label = { Text(label, maxLines = 1, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            ) { padding ->
                when {
                    selectedLearningCourseId != null -> in.urjakendra.app.ui.LiveLessonScreen(
                        courseId = selectedLearningCourseId!!,
                        isHindi = isHindi,
                        repository = repository,
                        sessionStore = sessionStore,
                        onBack = { selectedLearningCourseId = null },
                        modifier = Modifier.padding(padding)
                    )
                    selectedCourse != null -> CourseDetail(
                        course = selectedCourse!!,
                        isHindi = isHindi,
                        onBack = { selectedCourse = null; enrolMessage = null },
                        enrolMessage = enrolMessage,
                        onEnroll = {
                            val token = sessionStore.getToken()
                            if (token.isNullOrBlank()) {
                                enrolMessage = if (isHindi) "नामांकन के लिए लॉग इन करें।" else "Please sign in to enrol."
                            } else {
                                coroutineScope.launch {
                                    enrolMessage = if (isHindi) "नामांकन हो रहा है..." else "Enrolling..."
                                    try {
                                        repository.enroll(selectedCourse!!.backendId, token)
                                        enrolMessage = if (isHindi) "नामांकन सफल रहा। मेरी पढ़ाई देखें।" else "Enrolled successfully. Open My Learning."
                                    } catch (_: Exception) {
                                        enrolMessage = if (isHindi) "नामांकन नहीं हो सका। फिर प्रयास करें।" else "Could not enrol. Please try again."
                                    }
                                }
                            }
                        }
                    )
                    selectedCareer != null -> CareerDetail(selectedCareer!!, isHindi, onBack = { selectedCareer = null })
                    else -> when (selectedTab) {
                        0 -> HomeScreen(isHindi, onCourses = { selectedTab = 1 }, onCareers = { selectedTab = 3 }, onCourse = { selectedCourse = it })
                        1 -> LiveCoursesScreen(
                            isHindi = isHindi,
                            repository = in.urjakendra.app.data.UrjaRepository(),
                            onCourse = { selectedCourse = it },
                            modifier = Modifier.padding(padding)
                        )
                        2 -> LearningScreen(isHindi, onCourse = { selectedCourse = it }, modifier = Modifier.padding(padding))
                        3 -> CareersScreen(isHindi, onCareer = { selectedCareer = it }, modifier = Modifier.padding(padding))
                        else -> ProfileScreen(isHindi, onLanguage = { isHindi = !isHindi }, modifier = Modifier.padding(padding))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(isHindi: Boolean, onCourses: () -> Unit, onCareers: () -> Unit, onCourse: (Course) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8D8)), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text(if (isHindi) "स्वच्छ ऊर्जा कौशल" else "Clean Energy Skills", color = UrjaGreen, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(if (isHindi) "बेहतर कौशल। बेहतर करियर।" else "Build skills. Explore new career pathways.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = UrjaInk)
                    Spacer(Modifier.height(8.dp))
                    Text(if (isHindi) "सोलर • ईवी • बैटरी • चार्जिंग" else "Solar • EV • Batteries • EV Charging", color = UrjaInk)
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = onCourses, colors = ButtonDefaults.buttonColors(containerColor = UrjaOrange)) {
                        Text(if (isHindi) "कोर्स देखें" else "Explore Courses")
                    }
                }
            }
        }
        item { SectionTitle(if (isHindi) "कोर्स श्रेणियाँ" else "Course Categories") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryTile(if (isHindi) "सोलर" else "Solar", "☀", Modifier.weight(1f)) { onCourse(sampleCourses[0]) }
                CategoryTile("EV", "⚡", Modifier.weight(1f)) { onCourse(sampleCourses[1]) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryTile(if (isHindi) "बैटरी" else "Batteries", "▣", Modifier.weight(1f)) { onCourse(sampleCourses[2]) }
                CategoryTile(if (isHindi) "चार्जिंग" else "EV Charging", "↯", Modifier.weight(1f)) { onCourse(sampleCourses[3]) }
            }
        }
        item { SectionTitle(if (isHindi) "आगे सीखें" else "Continue Learning") }
        items(sampleCourses.filter { it.progress > 0 }) { course ->
            CourseRow(course, isHindi, onClick = { onCourse(course) })
        }
        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5E9)), modifier = Modifier.fillMaxWidth().clickable { onCareers() }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Work, contentDescription = null, tint = UrjaGreen, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(if (isHindi) "करियर के अवसर" else "Explore Career Pathways", fontWeight = FontWeight.Bold)
                        Text(if (isHindi) "अपना अगला कदम चुनें" else "Find a pathway that fits your goals.", fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun CoursesScreen(isHindi: Boolean, onCourse: (Course) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(if (isHindi) "कोर्स" else "Courses", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        Text(if (isHindi) "अपनी रुचि के अनुसार सीखें" else "Choose a course to explore.", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sampleCourses) { course -> CourseRow(course, isHindi) { onCourse(course) } }
        }
    }
}

@Composable
fun LearningScreen(isHindi: Boolean, onCourse: (Course) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text(if (isHindi) "मेरी पढ़ाई" else "My Learning", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(if (isHindi) "आपकी सीखने की प्रगति" else "Your learning progress", color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sampleCourses.filter { it.progress > 0 }) { course -> CourseRow(course, isHindi) { onCourse(course) } }
            item {
                if (sampleCourses.none { it.progress > 0 }) {
                    Text(if (isHindi) "अभी कोई कोर्स शुरू नहीं हुआ है।" else "You have not started a course yet.", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun CareersScreen(isHindi: Boolean, onCareer: (Career) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text(if (isHindi) "करियर विकल्प" else "Career Pathways", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(if (isHindi) "जानकारी और रुचि दर्ज करें। नौकरी की गारंटी नहीं है।" else "Explore options. Applications do not guarantee employment.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sampleCareers) { career ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onCareer(career) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkOutline, contentDescription = null, tint = UrjaGreen, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(if (isHindi) career.titleHi else career.titleEn, fontWeight = FontWeight.Bold)
                            Text(if (isHindi) career.descHi else career.descEn, fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun CourseDetail(
    course: Course,
    isHindi: Boolean,
    onBack: () -> Unit,
    enrolMessage: String? = null,
    onEnroll: () -> Unit = {}
) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        TextButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null); Spacer(Modifier.width(4.dp)); Text(if (isHindi) "वापस" else "Back") }
        Text(course.code, color = UrjaOrange, fontWeight = FontWeight.Bold)
        Text(if (isHindi) course.titleHi else course.titleEn, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        Text(if (isHindi) "${course.lessons} मॉड्यूल • शुल्क बाद में घोषित होगा" else "${course.lessons} modules • Fee to be announced", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(18.dp))
        Text(if (isHindi) "इस कोर्स में आप सीखेंगे" else "What you will learn", fontWeight = FontWeight.Bold)
        Text(if (isHindi) "मुख्य अवधारणाएँ, सुरक्षित कार्य पद्धतियाँ और करियर की जानकारी।" else "Core concepts, safe work practices, and career awareness.", modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(18.dp))
        Text(if (isHindi) "अंतिम मूल्यांकन पास करने के बाद प्रमाणपत्र उपलब्ध होगा।" else "Certificate available after passing the final assessment.", color = UrjaGreen)
        enrolMessage?.let { Text(it, color = if (it.contains("success", true) || it.contains("सफल")) UrjaGreen else UrjaInk, modifier = Modifier.padding(top = 12.dp)) }
        Spacer(Modifier.weight(1f))
        Button(onClick = onEnroll, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = UrjaOrange)) {
            Text(if (isHindi) "कोर्स में नामांकन करें" else "Enrol in Course")
        }
    }
}

@Composable
fun CareerDetail(career: Career, isHindi: Boolean, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp)) {
        TextButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null); Spacer(Modifier.width(4.dp)); Text(if (isHindi) "वापस" else "Back") }
        Icon(Icons.Default.WorkOutline, contentDescription = null, tint = UrjaGreen, modifier = Modifier.size(42.dp))
        Text(if (isHindi) career.titleHi else career.titleEn, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text(if (isHindi) career.descHi else career.descEn, modifier = Modifier.padding(top = 8.dp))
        Text(if (isHindi) "आवेदन जमा करना नौकरी या व्यवसाय की स्वीकृति की गारंटी नहीं है।" else "Submitting interest does not guarantee a job, franchise, or business approval.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "रुचि दर्ज करना जल्द उपलब्ध होगा" else "Interest form coming soon") }
    }
}

@Composable
fun ProfileScreen(isHindi: Boolean, onLanguage: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text(if (isHindi) "प्रोफ़ाइल" else "Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Row(Modifier.fillMaxWidth().clickable { onLanguage() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = UrjaOrange)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (isHindi) "भाषा बदलें" else "Language")
                    Text(if (isHindi) "हिंदी" else "English", fontSize = 12.sp, color = Color.Gray)
                }
                Text(if (isHindi) "EN" else "हिं", color = UrjaOrange, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(if (isHindi) "मोबाइल OTP लॉगिन, प्रोफ़ाइल और सहायता बैकएंड जुड़ने के बाद सक्रिय होंगे।" else "Mobile OTP login, profile data, and support activate after backend integration.", color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
fun CourseRow(course: Course, isHindi: Boolean, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).background(Color(0xFFFFE8D8), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(when(course.category) {
                    "Solar" -> Icons.Default.WbSunny
                    "EV" -> Icons.Default.ElectricCar
                    "Batteries" -> Icons.Default.BatteryChargingFull
                    else -> Icons.Default.EvStation
                }, contentDescription = null, tint = UrjaOrange, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(course.code, fontSize = 10.sp, color = UrjaOrange, fontWeight = FontWeight.Bold)
                Text(if (isHindi) course.titleHi else course.titleEn, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(if (isHindi) "${course.lessons} मॉड्यूल" else "${course.lessons} modules", fontSize = 12.sp, color = Color.Gray)
                if (course.progress > 0) {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(progress = { course.progress / 100f }, modifier = Modifier.fillMaxWidth(), color = UrjaGreen, trackColor = Color(0xFFE7ECE8))
                    Text("${course.progress}% ${if (isHindi) "पूरा" else "complete"}", fontSize = 10.sp, color = UrjaGreen)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun CategoryTile(title: String, symbol: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, fontSize = 26.sp, color = UrjaGreen)
            Spacer(Modifier.height(5.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = UrjaInk)
}
