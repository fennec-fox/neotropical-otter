package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.common.method.PaidCreditCard
import io.mustelidae.otter.neotropical.api.common.method.PaidDiscountCoupon
import io.mustelidae.otter.neotropical.api.common.method.PaidPoint
import java.time.LocalDateTime

data class PaidReceipt(
    val paidAmount: Long,
    val paidDate: LocalDateTime,
    val isCancel: Boolean,
    val creditCard: PaidCreditCard? = null,
    val point: PaidPoint? = null,
    val discountCoupon: PaidDiscountCoupon? = null,
    val refundAmount: Long? = null,
    val canceledDate: LocalDateTime? = null,
)
