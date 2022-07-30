package com.wutsi.platform.sms.endpoint

import com.wutsi.platform.sms.`delegate`.SendVerificationDelegate
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.sms.dto.SendVerificationResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SendVerificationController(
    public val `delegate`: SendVerificationDelegate,
) {
    @PostMapping("/v1/sms/verifications")
    @PreAuthorize(value = "hasAuthority('sms-verify')")
    public fun invoke(@Valid @RequestBody request: SendVerificationRequest): SendVerificationResponse =
        delegate.invoke(request)
}
