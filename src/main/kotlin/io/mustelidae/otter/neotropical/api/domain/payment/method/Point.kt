package io.mustelidae.otter.neotropical.api.domain.payment.method

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Common.PaymentMethod.Point")
open class Point(
    open val amount: Long
) : PayMethod {
    @JsonIgnore
    @Transient
    override var paymentMethod: PaymentMethod = PaymentMethod.POINT
        protected set

    @JsonIgnore
    @Transient
    override var isValid: Boolean = true
}
