package com.wutsi.platform.sms.util

import com.wutsi.platform.core.util.URN

enum class EventURN(val urn: String) {
    VERIFICATION_TO_SEND(URN.of("event", "sms", "verification-to-send").value)
}
