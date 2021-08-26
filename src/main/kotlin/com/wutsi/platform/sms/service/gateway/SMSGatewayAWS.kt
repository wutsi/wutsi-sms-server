package com.wutsi.platform.sms.service.gateway

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import org.slf4j.LoggerFactory

class SMSGatewayAWS(
    private val amazonSNS: AmazonSNS
) : SMSGateway {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMSGatewayAWS::class.java)
    }

    override fun send(phoneNumber: String, message: String): String {
        try {
            LOGGER.info("Sending SMS to $phoneNumber: ...")
            val result: PublishResult = amazonSNS.publish(
                PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(phoneNumber)
                    .withMessageAttributes(emptyMap())
            )
            return result.messageId
        } catch (ex: Exception) {
            throw SMSException(null, null, "Delivery failure to $phoneNumber", ex)
        }
    }
}
