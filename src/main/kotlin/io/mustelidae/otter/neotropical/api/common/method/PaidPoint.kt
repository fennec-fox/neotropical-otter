package io.mustelidae.otter.neotropical.api.common.method

import io.mustelidae.otter.neotropical.api.domain.payment.method.Point
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.Method.Paid.Point")
class PaidPoint(
    override val amount: Long,
    val name: String? = null,
    val refundAmount: Long? = null
) : Point(amount)
