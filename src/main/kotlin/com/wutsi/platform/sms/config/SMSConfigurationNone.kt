package com.wutsi.platform.sms.config

import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.service.gateway.SMSGatewayNone
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.application.sms.gateway.type"],
    havingValue = "none",
    matchIfMissing = true
)
public class SMSConfigurationNone {
    @Bean
    fun gateway(): SMSGateway =
        SMSGatewayNone()
}
