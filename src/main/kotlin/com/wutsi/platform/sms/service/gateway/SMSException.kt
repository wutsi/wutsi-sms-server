package com.wutsi.platform.sms.service.gateway

class SMSException(
    val code: String? = null, // Supplier error code
    val details: String? = null, // Supplier error details
    message: String? = null,
    ex: Throwable? = null
) : Exception(message, ex)
