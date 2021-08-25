package com.wutsi.platform.sms.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.EventURN
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.Locale

@Service
class EventHandler(
    private val mapper: ObjectMapper,
    private val dao: VerificationRepository,
    private val sms: SMSGateway,
    private val messageSource: MessageSource
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventHandler::class.java)
    }

    @EventListener
    fun onEvent(event: Event) {
        LOGGER.info("onEvent(${event.type})")
        if (event.type == EventURN.VERIFICATION_TO_SEND.urn) {
            val payload = mapper.readValue(event.payload, Map::class.java)
            send(payload["id"].toString().toLong())
        }
    }

    private fun send(id: Long) {
        val verif = dao.findById(id).get()
        val now = OffsetDateTime.now()
        if (now.isAfter(verif.expires)) {
            LOGGER.info("Verification#${verif.id} has expired since ${verif.expires}")
        } else {
            LOGGER.info("Sending verification code to ${verif.phoneNumber}")
            sms.send(
                phoneNumber = verif.phoneNumber,
                message = messageSource.getMessage("verification_message", arrayOf(verif.code), Locale(verif.language))
            )
        }
    }
}
