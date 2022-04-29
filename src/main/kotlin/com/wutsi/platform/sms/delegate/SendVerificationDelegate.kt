package com.wutsi.platform.sms.`delegate`

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.sms.dto.SendVerificationResponse
import com.wutsi.platform.sms.entity.VerificationEntity
import com.wutsi.platform.sms.entity.VerificationStatus
import com.wutsi.platform.sms.service.SMSService
import com.wutsi.platform.sms.util.ErrorURN.PHONE_NUMBER_MALFORMED
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import kotlin.math.pow

@Service
class SendVerificationDelegate(
    private val dao: VerificationRepository,
    private val sms: SMSService,
    private val logger: KVLogger,
) : AbstractDelegate() {
    fun invoke(request: SendVerificationRequest): SendVerificationResponse {
        logger.add("phone_number", request.phoneNumber)
        logger.add("language", request.language)

        try {
            /* Save */
            val util = PhoneNumberUtil.getInstance()
            val phoneNumber = util.parse(request.phoneNumber, "")
            val verification = dao.save(
                VerificationEntity(
                    phoneNumber = util.format(phoneNumber, E164),
                    language = request.language,
                    code = generateCode(6).toString(),
                    status = VerificationStatus.VERIFICATION_STATUS_PENDING,
                    created = OffsetDateTime.now(),
                    expires = OffsetDateTime.now().plusMinutes(15)
                )
            )
            logger.add("verification_id", verification.id)
            logger.add("verification_code", verification.code)
            logger.add("sms_id", verification.messageId)

            /* Push message */
            val testUser = isTestUser(request.phoneNumber)
            logger.add("test_user", testUser)
            if (!testUser) {
                sms.sendVerification(verification.id!!)
            }

            return SendVerificationResponse(
                id = verification.id ?: -1
            )
        } catch (ex: NumberParseException) {
            throw BadRequestException(
                error = Error(
                    code = PHONE_NUMBER_MALFORMED.urn,
                    parameter = Parameter(
                        name = "phoneNumber",
                        type = PARAMETER_TYPE_PAYLOAD,
                        value = request.phoneNumber
                    )
                ),
                ex
            )
        }
    }

    private fun generateCode(length: Int): Long {
        val factor = 10.0.pow(length.toDouble())
        while (true) {
            val value = (Math.random() * factor).toLong()
            if (value.toString().length == length)
                return value
        }
    }

    private fun isTestUser(phoneNumber: String, tenant: Tenant): Boolean =
        tenant.testPhoneNumbers.contains(phoneNumber)
}
