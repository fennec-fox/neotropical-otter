package io.mustelidae.otter.neotropical.api.domain.payment.method

sealed interface PayMethod {
    val paymentMethod: PaymentMethod
    var isValid: Boolean
}
