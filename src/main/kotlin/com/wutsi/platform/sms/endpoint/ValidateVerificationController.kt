package com.wutsi.platform.sms.endpoint

import com.wutsi.platform.sms.`delegate`.ValidateVerificationDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long
import kotlin.String

@RestController
public class ValidateVerificationController(
    public val `delegate`: ValidateVerificationDelegate,
) {
    @GetMapping("/v1/sms/verifications/{id}")
    @PreAuthorize(value = "hasAuthority('sms-verify')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @RequestParam(name = "code", required = false)
        code: String
    ) {
        delegate.invoke(id, code)
    }
}
