package in.urjakendra.app.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import in.urjakendra.app.data.*

/** Server-scored assessment UI. Wire into the lesson flow after enrollment and module completion. */
@Composable
fun ServerAssessmentScreen(
    courseId: String,
    isHindi: Boolean,
    repository: UrjaRepository,
    sessionStore: SessionStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var assessment by remember(courseId, isHindi) { mutableStateOf<AssessmentDto?>(null) }
    var answers by remember(courseId) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var result by remember(courseId) { mutableStateOf<AssessmentAttemptResultDto?>(null) }
    var certificateEligibility by remember(courseId) { mutableStateOf<CertificateEligibilityDto?>(null) }
    var certificateNumber by remember(courseId) { mutableStateOf<String?>(null) }
    var issuingCertificate by remember { mutableStateOf(false) }
    var downloadingCertificate by remember { mutableStateOf(false) }
    var certificateFilePath by remember(courseId) { mutableStateOf<String?>(null) }
    var pendingCertificateDownload by remember { mutableStateOf<PendingCertificateDownload?>(null) }
    val context = LocalContext.current
    var pendingCertificateIssue by remember { mutableStateOf<PendingCertificateIssue?>(null) }
    var loading by remember { mutableStateOf(true) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val locale = if (isHindi) "hi" else "en"
    var pendingSubmission by remember { mutableStateOf<PendingSubmission?>(null) }

    LaunchedEffect(courseId, locale) {
        val token = sessionStore.getToken()
        if (token.isNullOrBlank()) {
            error = if (isHindi) "कृपया पहले लॉग इन करें।" else "Please sign in first."
            loading = false
        } else {
            try { assessment = repository.getAssessment(courseId, token, locale) }
            catch (_: Exception) { error = if (isHindi) "मूल्यांकन लोड नहीं हुआ। नामांकन और सर्वर जाँचें।" else "Could not load assessment. Check enrollment and server." }
            // Restore certificate state when a learner returns to this screen after passing.
            // Eligibility lookup is best-effort so it cannot block the assessment itself.
            try { certificateEligibility = repository.getCertificateEligibility(courseId, token) }
            catch (_: Exception) { /* Keep assessment usable if certificate status is unavailable. */ }
            finally { loading = false }
        }
    }

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (isHindi) "सर्वर मूल्यांकन" else "Server assessment", style = MaterialTheme.typography.titleLarge)
        if (loading) CircularProgressIndicator()
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        assessment?.let { quiz ->
            Text(if (isHindi) "उत्तीर्ण अंक: ${quiz.thresholdPercent}%" else "Pass mark: ${quiz.thresholdPercent}%")
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(quiz.questions) { question ->
                    Card {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(questionTitle(question, isHindi), style = MaterialTheme.typography.titleSmall)
                            question.options.forEach { option ->
                                Button(
                                    onClick = { if (result == null && !submitting) answers = answers + (question.id to option.id) },
                                    enabled = result == null && !submitting,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text((if (answers[question.id] == option.id) "✓ " else "") + optionLabel(option, isHindi))
                                }
                            }
                        }
                    }
                }
            }
            // Returning learners may already have passed and received a certificate.
            // Show the server-backed certificate state even before they submit another attempt.
            if (result == null) {
                certificateEligibility?.let { eligibility ->
                    if (eligibility.certificateIssued || eligibility.eligibleForCertificate) {
                        Text(
                            if (eligibility.certificateIssued) {
                                if (isHindi) "प्रमाणपत्र जारी हो चुका है।" else "Certificate has already been issued."
                            } else {
                                if (isHindi) "आप प्रमाणपत्र के लिए पात्र हैं।" else "You are eligible for a certificate."
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                        (certificateNumber ?: eligibility.certificateNumber)?.let {
                            Text(if (isHindi) "प्रमाणपत्र संख्या: $it" else "Certificate number: $it")
                        }
                        if (eligibility.certificateIssued || certificateNumber != null) {
                            Button(
                                onClick = {
                                    val token = sessionStore.getToken() ?: return@Button
                                    downloadingCertificate = true
                                    error = null
                                    pendingCertificateDownload = PendingCertificateDownload(courseId, token)
                                },
                                enabled = !downloadingCertificate,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (downloadingCertificate) { if (isHindi) "डाउनलोड हो रहा है…" else "Downloading…" } else { if (isHindi) "PDF डाउनलोड करें" else "Download PDF" }) }
                            certificateFilePath?.let { path ->
                                Button(onClick = { openOrShareCertificate(context, path, share = false, isHindi = isHindi) }, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "PDF खोलें" else "Open PDF") }
                                Button(onClick = { openOrShareCertificate(context, path, share = true, isHindi = isHindi) }, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "PDF साझा करें" else "Share PDF") }
                            }
                        } else if (eligibility.eligibleForCertificate) {
                            Button(
                                onClick = {
                                    val token = sessionStore.getToken() ?: return@Button
                                    issuingCertificate = true
                                    error = null
                                    pendingCertificateIssue = PendingCertificateIssue(courseId, token)
                                },
                                enabled = !issuingCertificate,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (issuingCertificate) { if (isHindi) "जारी हो रहा है…" else "Issuing…" } else { if (isHindi) "प्रमाणपत्र जारी करें" else "Issue certificate" }) }
                        }
                    }
                }
            }
            result?.let {
                Text(
                    if (isHindi) "स्कोर: ${it.scorePercent}%. ${if (it.passed) "उत्तीर्ण" else "फिर प्रयास करें"}"
                    else "Score: ${it.scorePercent}%. ${if (it.passed) "Passed" else "Not passed"}",
                    style = MaterialTheme.typography.titleMedium
                )
                certificateEligibility?.let { eligibility ->
                    Text(
                        if (eligibility.eligibleForCertificate) {
                            if (isHindi) "आप प्रमाणपत्र के लिए पात्र हैं।" else "You are eligible for a certificate."
                        } else {
                            if (isHindi) eligibility.note else eligibility.note
                        },
                        color = if (eligibility.eligibleForCertificate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (eligibility.certificateIssued || certificateNumber != null) {
                        Text(if (isHindi) "प्रमाणपत्र जारी हो चुका है।" else "Certificate has been issued.")
                        (certificateNumber ?: eligibility.certificateNumber)?.let { Text(if (isHindi) "प्रमाणपत्र संख्या: $it" else "Certificate number: $it") }
                        Button(
                            onClick = {
                                val token = sessionStore.getToken() ?: return@Button
                                downloadingCertificate = true
                                error = null
                                pendingCertificateDownload = PendingCertificateDownload(courseId, token)
                            },
                            enabled = !downloadingCertificate,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (downloadingCertificate) { if (isHindi) "डाउनलोड हो रहा है…" else "Downloading…" } else { if (isHindi) "PDF डाउनलोड करें" else "Download PDF" }) }
                        certificateFilePath?.let { path ->
                            Button(onClick = { openOrShareCertificate(context, path, share = false, isHindi = isHindi) }, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "PDF खोलें" else "Open PDF") }
                            Button(onClick = { openOrShareCertificate(context, path, share = true, isHindi = isHindi) }, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "PDF साझा करें" else "Share PDF") }
                        }
                    } else if (eligibility.eligibleForCertificate) {
                        Button(
                            onClick = {
                                val token = sessionStore.getToken() ?: return@Button
                                issuingCertificate = true
                                error = null
                                pendingCertificateIssue = PendingCertificateIssue(courseId, token)
                            },
                            enabled = !issuingCertificate,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (issuingCertificate) { if (isHindi) "जारी हो रहा है…" else "Issuing…" } else { if (isHindi) "प्रमाणपत्र जारी करें" else "Issue certificate" }) }
                    }
                }
                Button(onClick = { answers = emptyMap(); result = null; certificateEligibility = null }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isHindi) "फिर प्रयास करें" else "Try again")
                }
            } ?: Button(
                onClick = {
                    val token = sessionStore.getToken() ?: return@Button
                    submitting = true
                    error = null
                    // Coroutine work is launched from a composition-owned effect below.
                    pendingSubmission = PendingSubmission(courseId, token, answers.map { AssessmentAnswerDto(it.key, it.value) })
                },
                enabled = !submitting && quiz.questions.isNotEmpty() && quiz.questions.all { answers.containsKey(it.id) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isHindi) "जमा करें" else "Submit") }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(if (isHindi) "वापस जाएँ" else "Back") }
    }

    LaunchedEffect(pendingCertificateDownload) {
        val pending = pendingCertificateDownload ?: return@LaunchedEffect
        pendingCertificateDownload = null
        try {
            val body = repository.downloadCertificatePdf(pending.courseId, pending.token)
            val directory = java.io.File(context.filesDir, "certificates").apply { mkdirs() }
            val file = java.io.File(directory, "urja_certificate_${pending.courseId.replace(Regex("[^A-Za-z0-9_-]"), "_")}.pdf")
            body.byteStream().use { input -> file.outputStream().use { output -> input.copyTo(output) } }
            certificateFilePath = file.absolutePath
            Toast.makeText(context, if (isHindi) "प्रमाणपत्र PDF डाउनलोड हो गया" else "Certificate PDF downloaded", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            error = if (isHindi) "PDF डाउनलोड नहीं हुआ। प्रमाणपत्र जारी हुआ है और सर्वर उपलब्ध है, यह जाँचें।" else "PDF download failed. Confirm the certificate is issued and the server is available."
        } finally {
            downloadingCertificate = false
        }
    }

    LaunchedEffect(pendingCertificateIssue) {
        val pending = pendingCertificateIssue ?: return@LaunchedEffect
        pendingCertificateIssue = null
        try {
            val issued = repository.issueCertificate(pending.courseId, pending.token)
            certificateNumber = issued.certificateNumber
            certificateEligibility = repository.getCertificateEligibility(pending.courseId, pending.token)
        } catch (_: Exception) {
            error = if (isHindi) "प्रमाणपत्र जारी नहीं हुआ। पात्रता और कनेक्शन जाँचें।" else "Certificate issuance failed. Check eligibility and connection, then retry."
        } finally {
            issuingCertificate = false
        }
    }

    var submissionToRun by remember { mutableStateOf<PendingSubmission?>(null) }
    // Bridge button intent into a LaunchedEffect so network calls remain lifecycle-aware.
    LaunchedEffect(pendingSubmission) {
        val pending = pendingSubmission ?: return@LaunchedEffect
        pendingSubmission = null
        submissionToRun = pending
    }
    LaunchedEffect(submissionToRun) {
        val pending = submissionToRun ?: return@LaunchedEffect
        try {
            val attempt = repository.submitAssessment(pending.courseId, pending.token, pending.answers)
            result = attempt
            if (attempt.passed) {
                certificateEligibility = repository.getCertificateEligibility(pending.courseId, pending.token)
            }
        } catch (_: Exception) {
            error = if (isHindi) "जमा नहीं हुआ या पात्रता जाँच नहीं हो सकी। कनेक्शन जाँचकर फिर प्रयास करें।"
            else "Submission or certificate eligibility check failed. Check connection and retry."
        } finally {
            submitting = false
            submissionToRun = null
        }
    }
}

private data class PendingSubmission(val courseId: String, val token: String, val answers: List<AssessmentAnswerDto>)
private data class PendingCertificateIssue(val courseId: String, val token: String)
private data class PendingCertificateDownload(val courseId: String, val token: String)

private fun openOrShareCertificate(context: android.content.Context, path: String, share: Boolean, isHindi: Boolean) {
    try {
        val file = java.io.File(path)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = if (share) Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, if (isHindi) "प्रमाणपत्र" else "Certificate"))
    } catch (_: Exception) {
        Toast.makeText(context, if (isHindi) "PDF खोलने या साझा करने के लिए उपयुक्त ऐप नहीं मिला।" else "No compatible app found to open or share the PDF.", Toast.LENGTH_LONG).show()
    }
}


private fun questionTitle(q: AssessmentQuestionDto, hindi: Boolean) =
    q.question?.takeIf { it.isNotBlank() }
        ?: if (hindi) q.hi ?: q.en.orEmpty() else q.en ?: q.hi.orEmpty()
private fun optionLabel(o: AssessmentOptionDto, hindi: Boolean) =
    o.text?.takeIf { it.isNotBlank() }
        ?: if (hindi) o.hi ?: o.en.orEmpty() else o.en ?: o.hi.orEmpty()
