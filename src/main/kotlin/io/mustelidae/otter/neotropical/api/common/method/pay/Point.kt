package io.mustelidae.otter.neotropical.api.common.method.pay

import com.fasterxml.jackson.annotation.JsonIgnore

open class Point(
    open val amount: Long
) : PayMethod {
    @JsonIgnore
    override val paymentMethod: PaymentMethod = PaymentMethod.POINT
}
