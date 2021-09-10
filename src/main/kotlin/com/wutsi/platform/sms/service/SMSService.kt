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
        val verification = dao.findById(id).get()
        val now = OffsetDateTime.now()
        if (now.isAfter(verification.expires)) {
            LOGGER.info("Verification#${verification.id} has expired since ${verification.expires}")
        } else {
            verification.messageId = sms.send(
                phoneNumber = verification.phoneNumber,
                message = messageSource.getMessage("verification_message", arrayOf(verification.code), Locale(verification.language))
            )
            dao.save(verification)

            LOGGER.info("Verification code sent to ${verification.phoneNumber}. messageId=${verification.messageId}")
        }
    }
}
