package com.wutsi.platform.sms.util

import com.wutsi.platform.core.util.URN

enum class ErrorURN(val urn: String) {
    PHONE_NUMBER_MALFORMED(URN.of("error", "sms", "phone-number-malformed").value),
    DELIVERY_FAILED(URN.of("error", "sms", "delivery-failed").value),
    VERIFICATION_FAILED(URN.of("error", "sms", "verification-failed").value),
    VERIFICATION_EXPIRED(URN.of("error", "sms", "verification-expired").value),
    VERIFICATION_ALREADY_VERIFIED(URN.of("error", "sms", "verification-already-verified").value)
}
