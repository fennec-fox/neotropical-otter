package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

class StableBillingPaymentMethodClient : BillingPaymentMethodClient {
    override fun findCard(userId: Long, payKey: String): BillingClientResources.Reply.CardDetail {
        TODO("Not yet implemented")
    }

    override fun findDiscountCoupon(
        userId: Long,
        couponId: Long,
        groupId: Long?
    ): BillingClientResources.Reply.CouponDetail {
        TODO("Not yet implemented")
    }

    override fun findAvailableCard(userId: Long): BillingClientResources.Reply.CardDetail {
        TODO("Not yet implemented")
    }
}
