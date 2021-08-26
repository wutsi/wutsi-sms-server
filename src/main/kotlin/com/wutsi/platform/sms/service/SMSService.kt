package com.wutsi.platform.sms.service

import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.service.gateway.SMSGateway
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.Locale
import javax.transaction.Transactional

@Service
class SMSService(
    private val dao: VerificationRepository,
    private val sms: SMSGateway,
    private val messageSource: MessageSource
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMSService::class.java)
    }

    @Transactional
    fun sendVerification(id: Long) {
        val verif = dao.findById(id).get()
        val now = OffsetDateTime.now()
        if (now.isAfter(verif.expires)) {
            LOGGER.info("Verification#${verif.id} has expired since ${verif.expires}")
        } else {
            LOGGER.info("Sending verification code to ${verif.phoneNumber}")
            verif.messageId = sms.send(
                phoneNumber = verif.phoneNumber,
                message = messageSource.getMessage("verification_message", arrayOf(verif.code), Locale(verif.language))
            )
            dao.save(verif)
        }
    }
}
