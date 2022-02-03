package io.mustelidae.otter.neotropical.api.common.method.pay

sealed interface PayMethod {
    val paymentMethod: PaymentMethod
}
