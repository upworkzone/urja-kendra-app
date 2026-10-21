package in.urjakendra.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import in.urjakendra.app.data.ProgressDto
import in.urjakendra.app.data.SessionStore
import in.urjakendra.app.data.UrjaRepository
import kotlinx.coroutines.launch

@Composable
fun LiveLessonScreen(
    courseId: String,
    isHindi: Boolean,
    repository: UrjaRepository,
    sessionStore: SessionStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember(courseId) { mutableStateOf<List<ProgressDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var expandedModule by remember(courseId) { mutableStateOf(1) }
    var showAssessment by remember(courseId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val locale = if (isHindi) "hi" else "en"

    suspend fun refresh() {
        val token = sessionStore.getToken()
        if (token.isNullOrBlank()) {
            error = if (isHindi) "जारी रखने के लिए लॉग इन करें।" else "Please sign in to continue."
            loading = false
            return
        }
        loading = true
        error = null
        try {
            progress = repository.getProgress(courseId, token)
        } catch (_: Exception) {
            error = if (isHindi) "प्रगति लोड नहीं हुई। फिर प्रयास करें।" else "Could not load progress. Please retry."
        } finally {
            loading = false
        }
    }

    suspend fun setModuleCompleted(module: Int, completed: Boolean) {
        val token = sessionStore.getToken() ?: run {
            error = if (isHindi) "कृपया लॉग इन करें।" else "Please sign in."
            return
        }
        try {
            repository.updateProgress(courseId, token, module, completed)
            refresh()
        } catch (_: Exception) {
            error = if (isHindi) "प्रगति सेव नहीं हुई। फिर प्रयास करें।" else "Progress was not saved. Please retry."
        }
    }

    LaunchedEffect(courseId, locale) { refresh() }

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack) { Text(if (isHindi) "वापस" else "Back") }
            Text(if (isHindi) "पाठ और प्रगति" else "Lessons & Progress", style = MaterialTheme.typography.titleLarge)
        }
        if (showAssessment && courseId.equals("ev-01", ignoreCase = true)) {
            ServerAssessmentScreen(
                courseId = courseId,
                isHindi = isHindi,
                repository = repository,
                sessionStore = sessionStore,
                onBack = { showAssessment = false }
            )
        } else when {
            loading -> CircularProgressIndicator()
            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = { scope.launch { refresh() } }) { Text(if (isHindi) "फिर प्रयास करें" else "Retry") }
            }
            else -> {
                val isEvPilot = courseId.equals("ev-01", ignoreCase = true)
                Text(if (isHindi) "पाठ और मॉड्यूल स्थिति" else "Lesson content & module progress")
                Text(if (isEvPilot) {
                    if (isHindi) "EV-01 के लिए प्रारंभिक पाठ उपलब्ध हैं। ये परिचयात्मक सामग्री हैं, हाई-वोल्टेज मरम्मत का प्रशिक्षण या प्रमाणन नहीं।"
                    else "Introductory lessons are available for EV-01. They do not qualify learners for high-voltage repair or certification."
                } else {
                    if (isHindi) "इस कोर्स की विस्तृत सामग्री अभी जोड़ी जानी है। नीचे मॉड्यूल प्रगति देख और अपडेट कर सकते हैं।"
                    else "Detailed content for this course is not yet loaded. You can still view and update module progress below."
                })
                if (isEvPilot) {
                    Button(onClick = { showAssessment = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isHindi) "EV-01 मूल्यांकन शुरू करें" else "Start EV-01 assessment")
                    }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items((1..8).toList(), key = { it }) { module ->
                        val current = progress.firstOrNull { it.moduleNumber == module }
                        val completed = current?.status == "COMPLETED"
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    if (isHindi) "मॉड्यूल $module" else "Module $module",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    if (completed) {
                                        if (isHindi) "पूरा हुआ" else "Completed"
                                    } else {
                                        if (isHindi) "अभी पूरा नहीं हुआ" else "Not completed"
                                    }
                                )
                                if (isEvPilot) {
                                    val lesson = PilotLessonContent.ev01[module - 1]
                                    Text(if (isHindi) lesson.titleHi else lesson.titleEn, style = MaterialTheme.typography.titleMedium)
                                    Button(onClick = { expandedModule = if (expandedModule == module) 0 else module }) {
                                        Text(if (expandedModule == module) { if (isHindi) "पाठ छिपाएँ" else "Hide lesson" } else { if (isHindi) "पाठ पढ़ें" else "Read lesson" })
                                    }
                                    if (expandedModule == module) {
                                        Text(if (isHindi) lesson.bodyHi else lesson.bodyEn)
                                        Spacer(Modifier.height(4.dp))
                                        Text(if (isHindi) "ज्ञान जाँच" else "Knowledge check", style = MaterialTheme.typography.titleSmall)
                                        Text(if (isHindi) lesson.checkHi else lesson.checkEn)
                                        Text(if (isHindi) "उत्तर: ${lesson.answerHi}" else "Answer: ${lesson.answerEn}")
                                    }
                                }
                                LinearProgressIndicator(
                                    progress = { if (completed) 1f else 0f },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(onClick = {
                                    scope.launch { setModuleCompleted(module, !completed) }
                                }) {
                                    Text(
                                        if (completed) {
                                            if (isHindi) "अधूरा चिह्नित करें" else "Mark incomplete"
                                        } else {
                                            if (isHindi) "पूरा चिह्नित करें" else "Mark completed"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


private data class PilotQuizQuestion(
    val questionEn: String,
    val questionHi: String,
    val choicesEn: List<String>,
    val choicesHi: List<String>,
    val correctIndex: Int
)

private val ev01AssessmentQuestions = listOf(
    PilotQuizQuestion("What stores propulsion energy in a battery-electric vehicle?", "बैटरी इलेक्ट्रिक वाहन में चलाने की ऊर्जा कहाँ संग्रहित होती है?", listOf("Traction battery", "Tyre", "Headlamp", "Seat"), listOf("ट्रैक्शन बैटरी", "टायर", "हेडलैम्प", "सीट"), 0),
    PilotQuizQuestion("Which component converts electrical energy into movement?", "विद्युत ऊर्जा को गति में कौन बदलता है?", listOf("Electric motor", "Windscreen", "Door lock", "Seat belt"), listOf("इलेक्ट्रिक मोटर", "विंडस्क्रीन", "डोर लॉक", "सीट बेल्ट"), 0),
    PilotQuizQuestion("What does regenerative braking do?", "रीजनरेटिव ब्रेकिंग क्या करती है?", listOf("Can recover some energy during deceleration", "Replaces all normal brakes", "Always fully charges the battery", "Removes need for safe driving"), listOf("गति कम करते समय कुछ ऊर्जा वापस ला सकती है", "सामान्य ब्रेक पूरी तरह बदल देती है", "बैटरी हमेशा पूरी चार्ज करती है", "सुरक्षित ड्राइविंग की जरूरत खत्म करती है"), 0),
    PilotQuizQuestion("What should be used to charge an EV?", "ईवी चार्ज करने के लिए क्या उपयोग करना चाहिए?", listOf("Compatible approved equipment", "Damaged cable", "Improvised bare wires", "Any unverified supply"), listOf("अनुकूल और अनुमोदित उपकरण", "क्षतिग्रस्त केबल", "खुले तारों का अस्थायी जोड़", "कोई भी अप्रमाणित सप्लाई"), 0),
    PilotQuizQuestion("Which factor can affect driving range?", "कौन-सा कारण ड्राइविंग रेंज को प्रभावित कर सकता है?", listOf("Weather and driving style", "Only paint colour", "Only seat fabric", "None of these"), listOf("मौसम और ड्राइविंग शैली", "केवल पेंट का रंग", "केवल सीट का कपड़ा", "इनमें से कोई नहीं"), 0),
    PilotQuizQuestion("If an EV shows smoke or signs of battery damage, what is the safe response?", "ईवी से धुआँ या बैटरी क्षति के संकेत हों तो सुरक्षित कदम क्या है?", listOf("Keep clear, follow emergency procedures and escalate", "Open the battery pack", "Touch cables to test them", "Continue normal operation"), listOf("दूर रहें, आपात प्रक्रिया अपनाएँ और विशेषज्ञ को सूचित करें", "बैटरी पैक खोलें", "जाँचने के लिए केबल छुएँ", "सामान्य काम जारी रखें"), 0),
    PilotQuizQuestion("Does regenerative braking remove the need for conventional brakes?", "क्या रीजनरेटिव ब्रेकिंग सामान्य ब्रेक की जरूरत खत्म करती है?", listOf("No, conventional brakes remain essential", "Yes, always", "Only in rain", "Only on highways"), listOf("नहीं, सामान्य ब्रेक जरूरी रहते हैं", "हाँ, हमेशा", "केवल बारिश में", "केवल हाईवे पर"), 0),
    PilotQuizQuestion("Before recommending an EV, what should you ask about?", "ईवी सुझाने से पहले किस बारे में पूछना चाहिए?", listOf("Daily travel and charging access", "Favourite colour only", "Nothing; every EV suits everyone", "Only the registration number"), listOf("दैनिक यात्रा और चार्जिंग सुविधा", "केवल पसंदीदा रंग", "कुछ नहीं; हर ईवी सभी के लिए सही है", "केवल पंजीकरण नंबर"), 0)
)

@Composable
private fun PilotAssessmentScreen(isHindi: Boolean, onBack: () -> Unit) {
    var answers by remember { mutableStateOf(List(ev01AssessmentQuestions.size) { -1 }) }
    var submitted by remember { mutableStateOf(false) }
    val passPercent = 70
    val score = ev01AssessmentQuestions.indices.count { answers[it] == ev01AssessmentQuestions[it].correctIndex }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (isHindi) "EV-01 मूल्यांकन" else "EV-01 Assessment", style = MaterialTheme.typography.titleLarge)
        Text(if (isHindi) "उत्तीर्ण होने के लिए कम से कम $passPercent% अंक चाहिए। यह अभ्यास मूल्यांकन है; परिणाम अभी सर्वर पर प्रमाणित या सेव नहीं होता।" else "Pass threshold: at least $passPercent%. This is a practice assessment; results are not yet verified or saved by the server.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(ev01AssessmentQuestions.size) { index ->
                val q = ev01AssessmentQuestions[index]
                Card {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${index + 1}. ${if (isHindi) q.questionHi else q.questionEn}", style = MaterialTheme.typography.titleSmall)
                        (if (isHindi) q.choicesHi else q.choicesEn).forEachIndexed { choiceIndex, choice ->
                            Button(
                                onClick = {
                                    if (!submitted) answers = answers.toMutableList().also { it[index] = choiceIndex }
                                },
                                enabled = !submitted,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text((if (answers[index] == choiceIndex) "✓ " else "") + choice)
                            }
                        }
                        if (submitted) {
                            Text(
                                if (answers[index] == q.correctIndex) {
                                    if (isHindi) "सही उत्तर" else "Correct"
                                } else {
                                    (if (isHindi) "सही उत्तर: " else "Correct answer: ") + (if (isHindi) q.choicesHi[q.correctIndex] else q.choicesEn[q.correctIndex])
                                },
                                color = if (answers[index] == q.correctIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
        if (submitted) {
            val passed = score * 100 >= passPercent * ev01AssessmentQuestions.size
            Text(
                if (isHindi) {
                    "आपका स्कोर: $score/${ev01AssessmentQuestions.size} (${score * 100 / ev01AssessmentQuestions.size}%). " +
                        if (passed) "अभ्यास सीमा पूरी हुई।" else "फिर से पढ़कर प्रयास करें।"
                } else {
                    "Your score: $score/${ev01AssessmentQuestions.size} (${score * 100 / ev01AssessmentQuestions.size}%). " +
                        if (passed) "Practice threshold reached." else "Review the lessons and try again."
                },
                style = MaterialTheme.typography.titleMedium
            )
            Button(onClick = { answers = List(ev01AssessmentQuestions.size) { -1 }; submitted = false }, modifier = Modifier.fillMaxWidth()) {
                Text(if (isHindi) "फिर प्रयास करें" else "Try again")
            }
        } else {
            Button(
                onClick = { submitted = true },
                enabled = answers.all { it >= 0 },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isHindi) "परिणाम देखें" else "Submit assessment") }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "पाठों पर वापस जाएँ" else "Back to lessons") }
    }
}
