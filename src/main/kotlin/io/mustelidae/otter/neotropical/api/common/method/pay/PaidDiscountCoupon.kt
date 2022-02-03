package io.mustelidae.otter.neotropical.api.common.method.pay

class PaidDiscountCoupon(
    override val id: Long,
    override val groupId: Long,
    val amount: Long,
    val name: String,
    val description: String
) : DiscountCoupon(id, groupId)
