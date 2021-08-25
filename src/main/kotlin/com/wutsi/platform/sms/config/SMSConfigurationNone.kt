package com.wutsi.platform.sms.config

import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.service.gateway.SMSGatewayNone
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.application.sms.gateway.type"],
    havingValue = "none",
    matchIfMissing = true
)
public class SMSConfigurationNone {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SMSConfigurationNone::class.java)
    }

    @Bean
    public fun gateway(): SMSGateway =
        SMSGatewayNone()

    @PostConstruct
    fun init() {
        LOGGER.info("Initializing SMSGateway")
    }
}
