package com.wutsi.platform.sms.dto

public data class SendVerificationRequest(
    public val phoneNumber: String = "",
    public val locale: String = "en"
)
