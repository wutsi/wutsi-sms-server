package com.wutsi.platform.sms.delegate

import com.wutsi.platform.sms.service.SecurityManager
import com.wutsi.platform.tenant.WutsiTenantApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
abstract class AbstractDelegate {
    @Autowired
    private lateinit var tenantApi: WutsiTenantApi

    @Autowired
    private lateinit var securityManager: SecurityManager

    protected fun isTestUser(phoneNumber: String): Boolean =
        try {
            tenantApi.getTenant(securityManager.tenantId())
                .tenant
                .testPhoneNumbers.contains(phoneNumber)
        } catch (ex: Exception) {
            false
        }
}
