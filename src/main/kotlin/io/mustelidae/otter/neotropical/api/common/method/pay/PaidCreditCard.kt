package io.mustelidae.otter.neotropical.api.common.method.pay

data class PaidCreditCard(
    override val payKey: String,
    val cardName: String,
    val cardNumber: String,
    val methodSummary: String? = null,
    val paidAmount: Long? = null,
    val refundAmount: Long? = null
) : CreditCard(payKey)
