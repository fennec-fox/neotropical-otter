package io.mustelidae.otter.neotropical.api.domain.payment.method

open class Point(
    open val amount: Long
) : PayMethod {
    @Transient
    override val paymentMethod: PaymentMethod = PaymentMethod.POINT
    @Transient
    override var isValid: Boolean = true
}
