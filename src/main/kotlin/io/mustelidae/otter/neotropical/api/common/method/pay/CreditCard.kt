package io.mustelidae.otter.neotropical.api.common.method.pay

import com.fasterxml.jackson.annotation.JsonIgnore

open class CreditCard(
    open val payKey: String
) : PayMethod {
    @JsonIgnore
    override val paymentMethod: PaymentMethod = PaymentMethod.CARD
}
