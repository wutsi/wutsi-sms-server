package com.wutsi.platform.sms.entity

import com.wutsi.platform.sms.entity.VerificationStatus.VERIFICATION_STATUS_INVALID
import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_VERIFICATION")
data class VerificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val code: String = "",
    val phoneNumber: String = "",
    val language: String = "en",

    @Enumerated
    var status: VerificationStatus = VERIFICATION_STATUS_INVALID,

    val created: OffsetDateTime = OffsetDateTime.now(),
    val expires: OffsetDateTime = OffsetDateTime.now().plusMinutes(5),
    var verified: OffsetDateTime? = null,
    var messageId: String? = null
)
