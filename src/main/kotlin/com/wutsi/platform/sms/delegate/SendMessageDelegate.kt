package com.wutsi.platform.sms.`delegate`

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.sms.dto.SendMessageRequest
import com.wutsi.platform.sms.dto.SendMessageResponse
import com.wutsi.platform.sms.service.gateway.SMSException
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.util.ErrorURN
import org.springframework.stereotype.Service

@Service
public class SendMessageDelegate(
    private val gateway: SMSGateway
) {
    public fun invoke(request: SendMessageRequest): SendMessageResponse {
        try {
            val util = PhoneNumberUtil.getInstance()
            val phoneNumber = util.parse(request.phoneNumber, "")
            return SendMessageResponse(
                id = gateway.send(
                    phoneNumber = util.format(phoneNumber, E164),
                    message = request.message
                )
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
