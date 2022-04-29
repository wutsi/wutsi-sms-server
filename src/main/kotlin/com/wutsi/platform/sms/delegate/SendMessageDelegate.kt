package com.wutsi.platform.sms.`delegate`

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.sms.dto.SendMessageRequest
import com.wutsi.platform.sms.dto.SendMessageResponse
import com.wutsi.platform.sms.service.gateway.SMSException
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.ErrorURN
import org.springframework.stereotype.Service

@Service
class SendMessageDelegate(
    private val gateway: SMSGateway,
    private val logger: KVLogger,
) : AbstractDelegate() {
    fun invoke(request: SendMessageRequest): SendMessageResponse {
        logger.add("phone_number", request.phoneNumber)
        logger.add("message", request.message)

        try {
            val util = PhoneNumberUtil.getInstance()
            val phoneNumber = util.parse(request.phoneNumber, "")
            val formattedPhoneNumber = util.format(phoneNumber, E164)
            val testUser = isTestUser(formattedPhoneNumber)

            val messageId = if (testUser)
                "-"
            else
                gateway.send(
                    phoneNumber = formattedPhoneNumber,
                    message = request.message
                )

            logger.add("msm_id", messageId)
            logger.add("formatted_phone_number", formattedPhoneNumber)
            logger.add("test_user", testUser)
            return SendMessageResponse(
                id = messageId
            )
        } catch (ex: NumberParseException) {
            throw BadRequestException(
                error = Error(
                    code = ErrorURN.PHONE_NUMBER_MALFORMED.urn,
                    message = ex.message,
                    parameter = Parameter(
                        name = "phoneNumber",
                        type = PARAMETER_TYPE_PAYLOAD,
                        value = request.phoneNumber
                    )
                ),
                ex
            )
        } catch (ex: SMSException) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.DELIVERY_FAILED.urn,
                    message = ex.message,
                    downstreamCode = ex.code,
                    downstreamMessage = ex.details
                ),
                ex
            )
        }
    }
}
