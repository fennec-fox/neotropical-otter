package io.mustelidae.otter.neotropical.api.common.method

import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.Method.Paid.CreditCard")
data class PaidCreditCard(
    override val payKey: String,
    override var cardName: String? = null,
    override var cardNumber: String? = null,
    val methodSummary: String? = null,
    val paidAmount: Long? = null,
    val refundAmount: Long? = null
) : CreditCard(payKey)
