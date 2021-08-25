package com.wutsi.platform.sms.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.String

public data class SendMessageRequest(
    public val phoneNumber: String = "",
    @get:NotBlank
    @get:Size(max = 160)
    public val message: String = ""
)
