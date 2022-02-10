package io.mustelidae.otter.neotropical.api.domain.payment.method

class PaidPoint(
    override val amount: Long,
    val name: String? = null,
    val refundAmount: Long? = null
) : Point(amount)
