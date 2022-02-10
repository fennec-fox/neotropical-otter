package io.mustelidae.otter.neotropical.api.domain.payment.method

data class PaidCreditCard(
    override val payKey: String,
    override var cardName: String? = null,
    override var cardNumber: String? = null,
    val methodSummary: String? = null,
    val paidAmount: Long? = null,
    val refundAmount: Long? = null
) : CreditCard(payKey)
