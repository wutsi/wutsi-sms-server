package com.wutsi.platform.sms.endpoint

import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import com.wutsi.platform.core.test.TestTracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.core.util.URN
import org.springframework.web.client.RestTemplate

abstract class AbstractSecuredController {
    open fun setUp() {
    }

    protected fun createResTemplate(
        scope: List<String> = emptyList(),
        subjectId: Long = -1,
        subjectType: SubjectType = USER,
        admin: Boolean = false
    ): RestTemplate {
        val rest = RestTemplate()

        val tokenProvider = TestTokenProvider(
            JWTBuilder(
                subject = subjectId.toString(),
                subjectType = subjectType,
                scope = scope,
                keyProvider = TestRSAKeyProvider(),
                admin = admin,
                name = URN.of("user", subjectId.toString()).value,
            ).build()
        )

        rest.interceptors.add(SpringTracingRequestInterceptor(TestTracingContext()))
        rest.interceptors.add(SpringAuthorizationRequestInterceptor(tokenProvider))
        return rest
    }
}
