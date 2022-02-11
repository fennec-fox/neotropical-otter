package io.mustelidae.otter.neotropical.api.domain.payment.method

open class Point(
    open val amount: Long
) : PayMethod {
    @Transient
    override var paymentMethod: PaymentMethod = PaymentMethod.POINT
        protected set
    @Transient
    override var isValid: Boolean = true
}
