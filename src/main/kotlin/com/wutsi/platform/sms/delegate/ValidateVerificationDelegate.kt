package com.wutsi.platform.sms.`delegate`

import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.sms.dao.VerificationRepository
import com.wutsi.platform.sms.entity.VerificationStatus.VERIFICATION_STATUS_VERIFIED
import com.wutsi.platform.sms.util.ErrorURN
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class ValidateVerificationDelegate(
    private val dao: VerificationRepository,
) : AbstractDelegate() {
    @Transactional
    fun invoke(id: Long, code: String) {
        val verification = dao.findById(id)
            .orElseThrow {
                failure(
                    code = ErrorURN.VERIFICATION_FAILED,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = PARAMETER_TYPE_PATH
                    )
                )
            }

        val now = OffsetDateTime.now()
        if (now.isAfter(verification.expires)) {
            throw failure(ErrorURN.VERIFICATION_EXPIRED)
        } else if (verification.status == VERIFICATION_STATUS_VERIFIED) {
            throw failure(ErrorURN.VERIFICATION_ALREADY_VERIFIED)
        } else if (code != verification.code && !isTestUser(verification.phoneNumber)) {
            throw failure(ErrorURN.VERIFICATION_FAILED)
        } else {
            verification.status = VERIFICATION_STATUS_VERIFIED
            verification.verified = now
            dao.save(verification)
        }
    }

    private fun failure(code: ErrorURN, parameter: Parameter? = null) =
        ConflictException(
            Error(
                code = code.urn,
                parameter = parameter
            )
        )
}
