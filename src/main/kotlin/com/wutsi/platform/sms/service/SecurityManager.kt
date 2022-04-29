package com.wutsi.platform.sms.service

import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service

@Service
class SecurityManager(
    private val tracingContext: TracingContext,
) {
    fun tenantId(): Long =
        tracingContext.tenantId()!!.toLong()
}
