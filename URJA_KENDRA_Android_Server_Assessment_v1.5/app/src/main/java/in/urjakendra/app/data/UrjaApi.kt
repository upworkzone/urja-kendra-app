package in.urjakendra.app.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.Streaming
import okhttp3.ResponseBody

interface UrjaApi {
    @GET("health")
    suspend fun health(): Map<String, String>

    @GET("api/v1/courses")
    suspend fun courses(@Query("locale") locale: String = "en"): List<CourseDto>

    @GET("api/v1/career-pathways")
    suspend fun careerPathways(@Query("locale") locale: String = "en"): List<CareerPathwayDto>

    @POST("api/v1/auth/otp/request")
    suspend fun requestOtp(@Body body: OtpRequestDto): OtpRequestResponse

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body body: OtpVerifyDto): TokenResponseDto

    @GET("api/v1/me")
    suspend fun me(@Header("Authorization") bearerToken: String): UserDto

    @POST("api/v1/courses/{courseId}/enroll")
    suspend fun enroll(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String
    ): EnrollmentDto

    @GET("api/v1/me/enrollments")
    suspend fun myEnrollments(
        @Header("Authorization") bearerToken: String,
        @Query("locale") locale: String = "en"
    ): List<EnrollmentDto>

    @GET("api/v1/courses/{courseId}/progress")
    suspend fun progress(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String
    ): List<ProgressDto>

    @PUT("api/v1/courses/{courseId}/progress")
    suspend fun updateProgress(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String,
        @Body body: ProgressUpdateDto
    ): ProgressDto
    @GET("api/v1/courses/{courseId}/assessment")
    suspend fun getAssessment(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String,
        @Query("locale") locale: String = "en"
    ): AssessmentDto

    @POST("api/v1/courses/{courseId}/assessment/attempts")
    suspend fun submitAssessment(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String,
        @Body body: AssessmentAttemptRequestDto
    ): AssessmentAttemptResultDto

    @GET("api/v1/me/courses/{courseId}/certificate-eligibility")
    suspend fun certificateEligibility(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String
    ): CertificateEligibilityDto

    @POST("api/v1/me/courses/{courseId}/certificate")
    suspend fun issueCertificate(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String
    ): CertificateIssueDto

    @Streaming
    @GET("api/v1/me/courses/{courseId}/certificate.pdf")
    suspend fun downloadCertificatePdf(
        @Path("courseId") courseId: String,
        @Header("Authorization") bearerToken: String
    ): ResponseBody

}
