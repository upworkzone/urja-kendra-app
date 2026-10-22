package `in`.urjakendra.app.data

/**
 * Thin repository wrapper for UI/ViewModel use.
 * Store access tokens with Android Keystore-backed encrypted storage before production.
 */
class UrjaRepository(private val api: UrjaApi = UrjaApiClient.api) {
    suspend fun getCourses(locale: String) = api.courses(locale)
    suspend fun getCareerPathways(locale: String) = api.careerPathways(locale)
    suspend fun requestOtp(phone: String, locale: String) =
        api.requestOtp(OtpRequestDto(phoneNumber = phone, locale = locale))
    suspend fun verifyOtp(phone: String, challengeId: String, otp: String) =
        api.verifyOtp(OtpVerifyDto(phoneNumber = phone, challengeId = challengeId, otp = otp))
    suspend fun getProfile(token: String) = api.me(bearer(token))
    suspend fun enroll(courseId: String, token: String) = api.enroll(courseId, bearer(token))
    suspend fun getMyEnrollments(token: String, locale: String) =
        api.myEnrollments(bearer(token), locale)
    suspend fun getProgress(courseId: String, token: String) =
        api.progress(courseId, bearer(token))
    suspend fun updateProgress(courseId: String, token: String, moduleNumber: Int, completed: Boolean) =
        api.updateProgress(
            courseId,
            bearer(token),
            ProgressUpdateDto(moduleNumber, if (completed) "COMPLETED" else "IN_PROGRESS")
        )


    suspend fun getAssessment(courseId: String, token: String, locale: String) =
        api.getAssessment(courseId, bearer(token), locale)
    suspend fun submitAssessment(courseId: String, token: String, answers: List<AssessmentAnswerDto>) =
        api.submitAssessment(courseId, bearer(token), AssessmentAttemptRequestDto(answers))
    suspend fun getCertificateEligibility(courseId: String, token: String) =
        api.certificateEligibility(courseId, bearer(token))
    suspend fun issueCertificate(courseId: String, token: String) =
        api.issueCertificate(courseId, bearer(token))
    suspend fun downloadCertificatePdf(courseId: String, token: String) =
        api.downloadCertificatePdf(courseId, bearer(token))

    private fun bearer(token: String) = "Bearer $token"
}
