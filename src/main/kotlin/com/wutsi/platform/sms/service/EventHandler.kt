package com.wutsi.platform.sms.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.sms.util.EventURN
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val mapper: ObjectMapper,
    private val sms: SMSService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventHandler::class.java)
    }

    @EventListener
    fun onEvent(event: Event) {
        LOGGER.info("onEvent(${event.type})")
        if (event.type == EventURN.VERIFICATION_TO_SEND.urn) {
            val payload = mapper.readValue(event.payload, Map::class.java)
            sms.sendVerification(payload["id"].toString().toLong())
        }
    }
}
