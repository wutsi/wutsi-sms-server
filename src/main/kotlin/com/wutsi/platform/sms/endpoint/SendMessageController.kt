package com.wutsi.platform.sms.endpoint

import com.wutsi.platform.sms.`delegate`.SendMessageDelegate
import com.wutsi.platform.sms.dto.SendMessageRequest
import com.wutsi.platform.sms.dto.SendMessageResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SendMessageController(
    public val `delegate`: SendMessageDelegate,
) {
    @PostMapping("/v1/sms/messages")
    @PreAuthorize(value = "hasAuthority('sms-send')")
    public fun invoke(@Valid @RequestBody request: SendMessageRequest): SendMessageResponse =
        delegate.invoke(request)
}
