package io.mustelidae.otter.neotropical.api.common.method.pay

class PaidPoint(
    override val amount: Long,
    val name: String? = null,
    val refundAmount: Long? = null
) : Point(amount)
