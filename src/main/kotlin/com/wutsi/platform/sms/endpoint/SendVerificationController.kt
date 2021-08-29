package com.wutsi.platform.sms.endpoint

import com.wutsi.platform.sms.`delegate`.SendVerificationDelegate
import com.wutsi.platform.sms.dto.SendVerificationRequest
import com.wutsi.platform.sms.dto.SendVerificationResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
@CrossOrigin(
    origins = ["*"],
    allowedHeaders = ["Content-Type", "Authorization", "Content-Length", "X-Requested-With"],
    methods = [
        org.springframework.web.bind.annotation.RequestMethod.GET,
        org.springframework.web.bind.annotation.RequestMethod.DELETE,
        org.springframework.web.bind.annotation.RequestMethod.OPTIONS,
        org.springframework.web.bind.annotation.RequestMethod.HEAD,
        org.springframework.web.bind.annotation.RequestMethod.POST,
        org.springframework.web.bind.annotation.RequestMethod.PUT
    ]
)
public class SendVerificationController(
    private val `delegate`: SendVerificationDelegate
) {
    @PostMapping("/v1/sms/verifications")
    @PreAuthorize(value = "hasAuthority('sms-verify')")
    public fun invoke(@Valid @RequestBody request: SendVerificationRequest): SendVerificationResponse =
        delegate.invoke(request)
}
