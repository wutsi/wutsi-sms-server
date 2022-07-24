package com.wutsi.platform.sms.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.dto.SendMessageResponse
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.sms.dto.SendVerificationResponse
import com.wutsi.platform.sms.entity.VerificationStatus
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.ErrorURN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql"])
class SendVerificationControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    lateinit var url: String

    @Autowired
    lateinit var dao: VerificationRepository

    @MockBean
    lateinit var sms: SMSGateway

    lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()
        url = "http://localhost:$port/v1/sms/verifications"

        doReturn(UUID.randomUUID().toString()).whenever(sms).send(any(), any())

        rest = createResTemplate(listOf("sms-verify"))
    }

    @Test
    fun `send verification request`() {
        // WHEN
        val request = SendVerificationRequest(
            phoneNumber = "+23774511111",
            language = "en"
        )
        val response = rest.postForEntity(url, request, SendVerificationResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val obj = dao.findById(response.body.id).get()
        assertEquals(request.phoneNumber, obj.phoneNumber)
        assertEquals(request.language, obj.language)
        assertTrue(obj.code.length == 6)
        assertNotNull(obj.created)
        assertEquals(VerificationStatus.VERIFICATION_STATUS_PENDING, obj.status)
        assertEquals(5, Duration.between(obj.created, obj.expires).toMinutes())
        assertNull(obj.verified)

        verify(sms).send(eq("+23774511111"), any())
    }

    @Test
    fun `never send verification to test phone number`() {
        // WHEN
        val request = SendVerificationRequest(
            phoneNumber = TEST_PHONE_NUMBER,
            language = "en"
        )
        val response = rest.postForEntity(url, request, SendVerificationResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)
        val obj = dao.findById(response.body.id).get()
        assertEquals(request.phoneNumber, obj.phoneNumber)
        assertEquals(request.language, obj.language)
        assertTrue(obj.code.length == 6)
        assertNotNull(obj.created)
        assertEquals(VerificationStatus.VERIFICATION_STATUS_PENDING, obj.status)
        assertEquals(5, Duration.between(obj.created, obj.expires).toMinutes())
        assertNull(obj.verified)

        verify(sms, never()).send(any(), any())
    }

    @Test
    fun `send verification request with invalid number`() {
        val request = SendVerificationRequest(
            phoneNumber = "00000",
            language = "en"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_MALFORMED.urn, response.error.code)
    }

    @Test
    fun `send verification request with invalid scope`() {
        val request = SendVerificationRequest(
            phoneNumber = "+23774511111",
            language = "en"
        )

        rest = createResTemplate(listOf("xxx"))
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        assertEquals(403, ex.rawStatusCode)
    }
}
