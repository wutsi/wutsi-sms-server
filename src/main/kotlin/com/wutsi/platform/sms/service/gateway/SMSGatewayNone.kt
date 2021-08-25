package com.wutsi.platform.sms.service.gateway

import org.slf4j.LoggerFactory
import java.util.UUID

class SMSGatewayNone : SMSGateway {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMSGatewayNone::class.java)
    }

    override fun send(number: String, message: String): String {
        LOGGER.info("To: $number \n$message")
        return UUID.randomUUID().toString()
    }
}
