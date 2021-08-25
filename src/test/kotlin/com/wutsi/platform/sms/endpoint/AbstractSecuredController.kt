package com.wutsi.platform.sms.endpoint

import com.auth0.jwt.interfaces.RSAKeyProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.core.test.TestRSAKeyProviderBuilder
import com.wutsi.platform.core.test.TestRestTemplateBuilder
import com.wutsi.platform.security.WutsiSecurityApi
import com.wutsi.platform.security.dto.GetKeyResponse
import com.wutsi.platform.security.dto.Key
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestTemplate
import java.util.Base64

abstract class AbstractSecuredController {
    private val keyProvider: RSAKeyProvider = TestRSAKeyProviderBuilder().build()

    @MockBean
    lateinit var securityAPI: WutsiSecurityApi

    @BeforeEach
    open fun setUp() {
        val key = Key(
            algorithm = "RSA",
            content = Base64.getEncoder().encodeToString(keyProvider.getPublicKeyById("1").encoded)
        )
        doReturn(GetKeyResponse(key)).whenever(securityAPI).getKey(any())
    }

    protected fun createResTemplate(scopes: List<String>): RestTemplate =
        TestRestTemplateBuilder(keyProvider, scopes).build()
}
