package com.wutsi.platform.sms.service.gateway

interface SMSGateway {
    @Throws(SMSException::class)
    fun send(phoneNumber: String, message: String): String
}
