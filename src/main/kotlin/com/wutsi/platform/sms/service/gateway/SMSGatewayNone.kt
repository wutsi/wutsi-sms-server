package com.wutsi.platform.sms.service.gateway

import org.slf4j.LoggerFactory
import java.util.UUID

class SMSGatewayNone : SMSGateway {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMSGatewayNone::class.java)
    }

    override fun send(phoneNumber: String, message: String): String {
        LOGGER.info("Sending SMS to $phoneNumber\n$message")
        return UUID.randomUUID().toString()
    }
}
