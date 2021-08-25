package com.wutsi.platform.sms.service.gateway

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

internal class SMSGatewayNoneTest {

    @Test
    fun send() {
        val id = SMSGatewayNone().send("+23799912346", "Hello world")
        assertNotNull(id)
    }
}
