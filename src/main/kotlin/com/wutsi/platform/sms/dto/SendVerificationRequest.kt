package com.wutsi.platform.sms.dto

import javax.validation.constraints.Size
import kotlin.String

public data class SendVerificationRequest(
    public val phoneNumber: String = "",
    @get:Size(max = 2)
    public val language: String = "en"
)
