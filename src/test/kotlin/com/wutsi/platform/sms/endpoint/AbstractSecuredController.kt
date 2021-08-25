package com.wutsi.platform.sms.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.GetKeyResponse
import com.wutsi.platform.security.dto.Key
import com.wutsi.platform.security.test.TestRSAKeyProvider
import com.wutsi.platform.security.test.TestRestTemplate
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.mock.mockito.MockBean

abstract class AbstractSecuredController {
    protected val keyProvider = TestRSAKeyProvider()

    @MockBean
    lateinit var securityAPI: WutsiSecurityApi

    @BeforeEach
    open fun setUp() {
        val key = Key(
            algorithm = "RSA",
            content = keyProvider.getPublicKeyAsString()
        )
        doReturn(GetKeyResponse(key)).whenever(securityAPI).getKey(any())
    }

    protected fun createResTemplate(scopes: List<String>): TestRestTemplate =
        TestRestTemplate(scopes = scopes, keyProvider = keyProvider)
}
