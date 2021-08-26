package com.wutsi.platform.sms.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.EventURN
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
internal class EventHandlerTest {
    @Autowired
    private lateinit var handler: EventHandler

    @Autowired
    private lateinit var dao: VerificationRepository

    @MockBean
    private lateinit var sms: SMSGateway

    @Test
    fun `handle request`() {
        doReturn("0000-1111").whenever(sms).send(any(), any())

        val event = Event(
            type = EventURN.VERIFICATION_TO_SEND.urn,
            payload = """
                {
                    "id": 100
                }
            """.trimIndent()
        )
        handler.onEvent(event)

        verify(sms).send("+23774511100", "Code de verification Wutsi: 000000")

        val verif = dao.findById(100).get()
        assertEquals("0000-1111", verif.messageId)
    }

    @Test
    fun `handle expired request`() {
        val event = Event(
            type = EventURN.VERIFICATION_TO_SEND.urn,
            payload = """
                {
                    "id": 199
                }
            """.trimIndent()
        )
        handler.onEvent(event)

        verify(sms, never()).send(any(), any())
    }
}
