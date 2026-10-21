package in.urjakendra.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import in.urjakendra.app.data.CourseDto
import in.urjakendra.app.data.UrjaRepository
import in.urjakendra.app.Course
import in.urjakendra.app.CourseRow
import kotlinx.coroutines.launch

@Composable
fun LiveCoursesScreen(
    isHindi: Boolean,
    repository: UrjaRepository,
    onCourse: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    var courses by remember { mutableStateOf<List<CourseDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val locale = if (isHindi) "hi" else "en"

    suspend fun loadCourses() {
        loading = true
        error = null
        try {
            courses = repository.getCourses(locale).filter { it.published }
        } catch (_: Exception) {
            error = if (isHindi) {
                "कोर्स लोड नहीं हो सके। इंटरनेट कनेक्शन जाँचें और फिर प्रयास करें।"
            } else {
                "Courses could not be loaded. Check your connection and retry."
            }
        } finally {
            loading = false
        }
    }

    LaunchedEffect(locale) { loadCourses() }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            if (isHindi) "कोर्स" else "Courses",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            if (isHindi) "अपनी रुचि के अनुसार सीखें" else "Choose a course to explore.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            loading -> CircularProgressIndicator()
            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = { scope.launch { loadCourses() } }) {
                    Text(if (isHindi) "फिर प्रयास करें" else "Retry")
                }
            }
            courses.isEmpty() -> Text(
                if (isHindi) "अभी कोई प्रकाशित कोर्स उपलब्ध नहीं है।"
                else "No published courses are available yet."
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(courses, key = { it.id }) { dto ->
                    val course = Course(
                        code = dto.code,
                        titleEn = dto.title,
                        titleHi = dto.title,
                        category = dto.category,
                        lessons = dto.moduleCount,
                        progress = 0,
                        backendId = dto.id
                    )
                    CourseRow(course, isHindi) { onCourse(course) }
                }
            }
        }
    }
}
