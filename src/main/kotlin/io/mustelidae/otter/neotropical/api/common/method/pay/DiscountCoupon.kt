package io.mustelidae.otter.neotropical.api.common.method.pay

import com.fasterxml.jackson.annotation.JsonIgnore

open class DiscountCoupon(
    open val id: Long,
    open val groupId: Long,
) : PayMethod {
    @JsonIgnore
    override val paymentMethod: PaymentMethod = PaymentMethod.DISCOUNT_COUPON
}
