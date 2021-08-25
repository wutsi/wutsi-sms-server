package com.wutsi.platform.sms.`delegate`

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.sms.dto.SendVerificationResponse
import com.wutsi.platform.sms.entity.VerificationEntity
import com.wutsi.platform.sms.entity.VerificationStatus
import com.wutsi.platform.sms.util.ErrorURN.PHONE_NUMBER_MALFORMED
import com.wutsi.platform.sms.util.EventURN
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
public class SendVerificationDelegate(
    private val dao: VerificationRepository,
    private val eventStream: EventStream
) {
    public fun invoke(request: SendVerificationRequest): SendVerificationResponse {
        try {
            /* Save */
            val util = PhoneNumberUtil.getInstance()
            val phoneNumber = util.parse(request.phoneNumber, "")
            val verification = dao.save(
                VerificationEntity(
                    phoneNumber = util.format(phoneNumber, E164),
                    language = request.locale,
                    code = (Math.random() * 1000000).toLong().toString(),
                    status = VerificationStatus.VERIFICATION_STATUS_PENDING,
                    created = OffsetDateTime.now(),
                    expires = OffsetDateTime.now().plusMinutes(15)
                )
            )

            /* Push message */
            eventStream.enqueue(
                type = EventURN.VERIFICATION_TO_SEND.urn,
                payload = mapOf("id" to verification.id)
            )

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
}
