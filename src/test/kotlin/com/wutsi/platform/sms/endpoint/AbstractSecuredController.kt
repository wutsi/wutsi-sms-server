package com.wutsi.platform.sms.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import com.wutsi.platform.core.test.TestTracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.core.util.URN
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.GetTenantResponse
import com.wutsi.platform.tenant.dto.Logo
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestTemplate

abstract class AbstractSecuredController {
    companion object {
        const val TEST_USER_ID = 777L
        const val TEST_PHONE_NUMBER = "+15147777777"
    }
    @MockBean
    protected lateinit var tenantApi: WutsiTenantApi

    open fun setUp() {
        val tenant = Tenant(
            id = 1,
            name = "test",
            logos = listOf(
                Logo(type = "PICTORIAL", url = "http://www.goole.com/images/1.png")
            ),
            countries = listOf("CM"),
            languages = listOf("en", "fr"),
            currency = "XAF",
            domainName = "www.wutsi.com",
            testUserIds = listOf(TEST_USER_ID),
            testPhoneNumbers = listOf(TEST_PHONE_NUMBER)
        )
        doReturn(GetTenantResponse(tenant)).whenever(tenantApi).getTenant(any())
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
