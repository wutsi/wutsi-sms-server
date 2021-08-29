package com.wutsi.platform.sms.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.entity.VerificationStatus
import com.wutsi.platform.sms.util.ErrorURN
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ValidateVerificationController.sql"])
public class ValidateVerificationControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    lateinit var dao: VerificationRepository

    var rest = createResTemplate(listOf("sms-verify"))

    @Test
    public fun `verify happy path`() {
        val url = "http://localhost:$port/v1/sms/verifications/100?code=000000"
        val response = rest.getForEntity(url, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val obj = dao.findById(100).get()
        assertNotNull(obj.verified)
        assertEquals(VerificationStatus.VERIFICATION_STATUS_VERIFIED, obj.status)
    }

    @Test
    public fun `verify - code mismatch`() {
        val url = "http://localhost:$port/v1/sms/verifications/101?code=xxx"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.VERIFICATION_FAILED.urn, response.error.code)
    }

    @Test
    public fun `verify - invalid ID`() {
        val url = "http://localhost:$port/v1/sms/verifications/999999?code=xxx"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.VERIFICATION_FAILED.urn, response.error.code)
    }

    @Test
    public fun `verify - already verified`() {
        val url = "http://localhost:$port/v1/sms/verifications/102?code=xxx"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.VERIFICATION_ALREADY_VERIFIED.urn, response.error.code)
    }

    @Test
    public fun `verify - expired`() {
        val url = "http://localhost:$port/v1/sms/verifications/103?code=333333"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }
        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsByteArray, ErrorResponse::class.java)
        assertEquals(ErrorURN.VERIFICATION_EXPIRED.urn, response.error.code)
    }
}
