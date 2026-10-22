package `in`.urjakendra.app.data

import com.google.gson.annotations.SerializedName

data class CourseDto(
    val id: String,
    val code: String,
    val category: String,
    val title: String,
    val summary: String,
    @SerializedName("module_count") val moduleCount: Int,
    val published: Boolean
)

data class CareerPathwayDto(
    val code: String,
    val title: String,
    val description: String
)

data class OtpRequestDto(
    @SerializedName("phone_number") val phoneNumber: String,
    val locale: String = "en"
)

data class OtpRequestResponse(
    @SerializedName("challenge_id") val challengeId: String,
    val message: String,
    @SerializedName("development_only_code") val developmentOnlyCode: String? = null
)

data class OtpVerifyDto(
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("challenge_id") val challengeId: String,
    val otp: String
)

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class EnrollmentDto(
    val id: String,
    @SerializedName("course_id") val courseId: String,
    @SerializedName("course_code") val courseCode: String,
    @SerializedName("course_title") val courseTitle: String,
    val status: String,
    @SerializedName("completed_modules") val completedModules: Int,
    @SerializedName("total_modules") val totalModules: Int,
    @SerializedName("progress_percent") val progressPercent: Int
)

data class ProgressDto(
    @SerializedName("module_number") val moduleNumber: Int,
    val status: String,
    @SerializedName("completed_at") val completedAt: String?,
    @SerializedName("updated_at") val updatedAt: String
)

data class ProgressUpdateDto(
    @SerializedName("module_number") val moduleNumber: Int,
    val status: String
)

data class UserDto(
    val id: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("preferred_locale") val preferredLocale: String
)


/** Server-authored assessment payload. Correct answers are intentionally not included. */
data class AssessmentDto(
    @SerializedName("course_id") val courseId: String,
    val title: String,
    @SerializedName("threshold_percent") val thresholdPercent: Int,
    val questions: List<AssessmentQuestionDto>
)
data class AssessmentQuestionDto(
    val id: String,
    // The current backend returns a locale-specific `question`; `en`/`hi`
    // remain supported for future bilingual payloads and test fixtures.
    val question: String? = null,
    val en: String? = null,
    val hi: String? = null,
    val options: List<AssessmentOptionDto>
)
data class AssessmentOptionDto(
    val id: String,
    // The current backend returns a locale-specific `text` for each option.
    val text: String? = null,
    val en: String? = null,
    val hi: String? = null
)
data class AssessmentAnswerDto(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("option_id") val optionId: String
)
data class AssessmentAttemptRequestDto(val answers: List<AssessmentAnswerDto>)
data class AssessmentAttemptResultDto(
    val id: String,
    @SerializedName("score_percent") val scorePercent: Int,
    @SerializedName("correct_count") val correctCount: Int,
    @SerializedName("question_count") val questionCount: Int,
    val passed: Boolean,
    @SerializedName("threshold_percent") val thresholdPercent: Int
)
data class CertificateEligibilityDto(
    @SerializedName("course_id") val courseId: String,
    @SerializedName("completed_modules") val completedModules: Int,
    @SerializedName("total_modules") val totalModules: Int,
    @SerializedName("latest_assessment_passed") val latestAssessmentPassed: Boolean,
    @SerializedName("eligible_for_certificate") val eligibleForCertificate: Boolean,
    @SerializedName("certificate_issued") val certificateIssued: Boolean,
    @SerializedName("certificate_number") val certificateNumber: String? = null,
    val note: String
)


data class CertificateIssueDto(
    val id: String,
    @SerializedName("certificate_number") val certificateNumber: String,
    @SerializedName("course_id") val courseId: String,
    val issued: Boolean,
    @SerializedName("download_path") val downloadPath: String?
)
