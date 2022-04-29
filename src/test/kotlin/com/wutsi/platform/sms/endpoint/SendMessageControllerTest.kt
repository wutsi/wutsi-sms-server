package com.wutsi.platform.sms.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.sms.dto.SendMessageRequest
import com.wutsi.platform.sms.dto.SendMessageResponse
import com.wutsi.platform.sms.service.gateway.SMSException
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.ErrorURN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMessageControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @MockBean
    lateinit var gateway: SMSGateway

    lateinit var url: String

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()
        url = "http://localhost:$port/v1/sms/messages"
        rest = createResTemplate(listOf("sms-send"))
    }

    @Test
    fun `send message`() {
        val messageId = UUID.randomUUID().toString()
        doReturn(messageId).whenever(gateway).send(any(), any())

        val request = SendMessageRequest(
            phoneNumber = "+23774511111",
            message = "Hello world"
        )
        val response = rest.postForEntity(url, request, SendMessageResponse::class.java)

        assertEquals(200, response.statusCodeValue)
        assertEquals(messageId, response.body.id)
        verify(gateway).send(request.phoneNumber, request.message)
    }

    @Test
    fun `never send SMS to test phone numbers`() {
        val request = SendMessageRequest(
            phoneNumber = TEST_PHONE_NUMBER,
            message = "Hello world"
        )
        val response = rest.postForEntity(url, request, SendMessageResponse::class.java)

        assertEquals(200, response.statusCodeValue)
        assertEquals("-", response.body.id)
        verify(gateway, never()).send(any(), any())
    }

    @Test
    fun `send message with invalid phone number`() {
        val request = SendMessageRequest(
            phoneNumber = "1111",
            message = "Hello world"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_MALFORMED.urn, response.error.code)
    }

    @Test
    fun `send message with sms error`() {
        doThrow(SMSException::class).whenever(gateway).send(any(), any())

        val request = SendMessageRequest(
            phoneNumber = "+23774511111",
            message = "Hello world"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.DELIVERY_FAILED.urn, response.error.code)
    }

    @Test
    fun `send message with invalid permission`() {
        val request = SendMessageRequest(
            phoneNumber = "+23774511111",
            message = "Hello world"
        )
        rest = createResTemplate(listOf("bad-scope"))
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        assertEquals(403, ex.rawStatusCode)
    }

    @Test
    fun `send message with unauthenticated`() {
        val request = SendMessageRequest(
            phoneNumber = "+23774511111",
            message = "Hello world"
        )
        rest = RestTemplate()
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        assertEquals(401, ex.rawStatusCode)
    }
}
