package io.mustelidae.otter.neotropical.api.common.method

import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.Method.Paid.DiscountCoupon")
class PaidDiscountCoupon(
    override val id: Long,
    override val groupId: Long?,
    val amount: Long,
    override var name: String? = null,
    val description: String? = null
) : DiscountCoupon(id, groupId)
