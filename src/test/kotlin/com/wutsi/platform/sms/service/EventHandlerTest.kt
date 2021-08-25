package com.wutsi.platform.sms.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.EventURN
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
internal class EventHandlerTest {
    @Autowired
    private lateinit var handler: EventHandler

    @MockBean
    private lateinit var sms: SMSGateway

    @Test
    fun `handle request`() {
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
