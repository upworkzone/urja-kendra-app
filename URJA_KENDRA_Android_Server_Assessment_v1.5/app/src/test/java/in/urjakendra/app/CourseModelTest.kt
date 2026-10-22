package `in`.urjakendra.app

import com.google.gson.Gson
import `in`.urjakendra.app.data.AssessmentDto
import `in`.urjakendra.app.data.CertificateEligibilityDto
import `in`.urjakendra.app.data.CourseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseModelTest {
    private val gson = Gson()

    @Test fun courseDtoMapsSnakeCaseApiFields() {
        val json = """{"id":"course-1","code":"EV-01","category":"EV","title":"EV Fundamentals","summary":"Intro","module_count":4,"published":true}"""
        val course = gson.fromJson(json, CourseDto::class.java)
        assertEquals("course-1", course.id)
        assertEquals("EV-01", course.code)
        assertEquals(4, course.moduleCount)
        assertTrue(course.published)
    }

    @Test fun certificateEligibilityMapsIssuedStateAndCertificateNumber() {
        val json = """{"course_id":"course-1","completed_modules":4,"total_modules":4,"latest_assessment_passed":true,"eligible_for_certificate":true,"certificate_issued":true,"certificate_number":"URJA-2026-0001","note":"Already issued"}"""
        val status = gson.fromJson(json, CertificateEligibilityDto::class.java)
        assertTrue(status.eligibleForCertificate)
        assertTrue(status.certificateIssued)
        assertEquals("URJA-2026-0001", status.certificateNumber)
    }

    @Test fun certificateNumberCanBeAbsentBeforeIssuance() {
        val json = """{"course_id":"course-1","completed_modules":2,"total_modules":4,"latest_assessment_passed":false,"eligible_for_certificate":false,"certificate_issued":false,"certificate_number":null,"note":"Requirements incomplete"}"""
        val status = gson.fromJson(json, CertificateEligibilityDto::class.java)
        assertFalse(status.eligibleForCertificate)
        assertFalse(status.certificateIssued)
        assertNull(status.certificateNumber)
    }

    @Test fun assessmentPayloadMatchesCurrentBackendLocalizedShape() {
        val json = """{"course_id":"ev-01","title":"EV-01 Assessment","threshold_percent":70,"questions":[{"id":"evq1","question":"Which component stores energy?","options":[{"id":"a","text":"Traction battery"},{"id":"b","text":"Radiator"}]}]}"""
        val assessment = gson.fromJson(json, AssessmentDto::class.java)
        assertEquals("Which component stores energy?", assessment.questions.first().question)
        assertEquals("Traction battery", assessment.questions.first().options.first().text)
    }

    @Test fun assessmentPayloadParsesServerAuthoredQuestionsAndOptions() {
        val json = """{"course_id":"course-1","title":"EV assessment","threshold_percent":70,"questions":[{"id":"q1","en":"What is EV?","hi":"ईवी क्या है?","options":[{"id":"a","en":"Electric vehicle","hi":"इलेक्ट्रिक वाहन"}]}]}"""
        val assessment = gson.fromJson(json, AssessmentDto::class.java)
        assertEquals(1, assessment.questions.size)
        assertEquals("What is EV?", assessment.questions.first().en)
        assertEquals("a", assessment.questions.first().options.first().id)
    }
}
