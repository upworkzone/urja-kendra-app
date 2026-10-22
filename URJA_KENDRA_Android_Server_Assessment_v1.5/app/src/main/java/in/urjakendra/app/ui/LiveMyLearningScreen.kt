package `in`.urjakendra.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import `in`.urjakendra.app.Course
import `in`.urjakendra.app.data.EnrollmentDto
import `in`.urjakendra.app.data.SessionStore
import `in`.urjakendra.app.data.UrjaRepository
import kotlinx.coroutines.launch

@Composable
fun LiveMyLearningScreen(
    isHindi: Boolean,
    repository: UrjaRepository,
    sessionStore: SessionStore,
    onCourse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var enrollments by remember { mutableStateOf<List<EnrollmentDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val locale = if (isHindi) "hi" else "en"

    suspend fun load() {
        val token = sessionStore.getToken()
        if (token.isNullOrBlank()) {
            enrollments = emptyList()
            error = if (isHindi) "अपनी पढ़ाई देखने के लिए लॉग इन करें।" else "Sign in to view your learning."
            loading = false
            return
        }
        loading = true
        error = null
        try {
            enrollments = repository.getMyEnrollments(token, locale)
        } catch (_: Exception) {
            error = if (isHindi) "आपकी पढ़ाई लोड नहीं हुई। फिर प्रयास करें।" else "Could not load your learning. Please retry."
        } finally {
            loading = false
        }
    }

    LaunchedEffect(locale) { load() }

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (isHindi) "मेरी पढ़ाई" else "My Learning", style = MaterialTheme.typography.headlineSmall)
        Text(if (isHindi) "आपकी नामांकित कक्षाएँ और प्रगति" else "Your enrolled courses and progress")
        when {
            loading -> CircularProgressIndicator()
            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = { scope.launch { load() } }) { Text(if (isHindi) "फिर प्रयास करें" else "Retry") }
            }
            enrollments.isEmpty() -> Text(if (isHindi) "आपने अभी किसी कोर्स में नामांकन नहीं किया है।" else "You have not enrolled in any courses yet.")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(enrollments, key = { it.id }) { enrollment ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(enrollment.courseTitle, style = MaterialTheme.typography.titleMedium)
                        Text(if (isHindi) "${enrollment.completedModules}/${enrollment.totalModules} मॉड्यूल पूरे" else "${enrollment.completedModules}/${enrollment.totalModules} modules completed")
                        LinearProgressIndicator(
                            progress = { enrollment.progressPercent.coerceIn(0, 100) / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("${enrollment.progressPercent.coerceIn(0, 100)}%")
                        Button(onClick = { onCourse(enrollment.courseId) }) {
                            Text(if (isHindi) "कोर्स खोलें" else "Open course")
                        }
                    }
                }
            }
        }
    }
}
