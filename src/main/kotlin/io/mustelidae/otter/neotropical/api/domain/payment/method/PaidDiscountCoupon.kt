package io.mustelidae.otter.neotropical.api.domain.payment.method

class PaidDiscountCoupon(
    override val id: Long,
    override val groupId: Long?,
    val amount: Long,
    override var name: String? = null,
    val description: String? = null
) : DiscountCoupon(id, groupId)
