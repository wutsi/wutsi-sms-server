package com.wutsi.platform.sms.config

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClient
import com.wutsi.platform.sms.service.gateway.SMSGateway
import com.wutsi.platform.sms.service.gateway.SMSGatewayAWS
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["wutsi.application.sms.gateway.type"],
    havingValue = "aws",
    matchIfMissing = true
)
public class SMSConfigurationAWS(
    @Value("\${wutsi.application.sms.gateway.aws.region") private val region: String
) {
    @Bean
    fun amazonSNS(): AmazonSNS =
        AmazonSNSClient.builder()
            .withRegion(region)
            .build()

    @Bean
    fun gateway(): SMSGateway =
        SMSGatewayAWS(amazonSNS())
}
