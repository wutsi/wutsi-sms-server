package com.wutsi.platform.sms

import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.`annotation`.EnableAsync
import org.springframework.scheduling.`annotation`.EnableScheduling
import org.springframework.transaction.`annotation`.EnableTransactionManagement
import kotlin.String

@WutsiApplication
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class Application

public fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
