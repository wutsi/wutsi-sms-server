package com.wutsi.platform.sms.dao

import com.wutsi.platform.sms.entity.VerificationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VerificationRepository : CrudRepository<VerificationEntity, Long>
