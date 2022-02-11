package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

interface BillingPaymentMethodClient {

    fun findCard(userId: Long, payKey: String): BillingClientResources.Reply.CardDetail

    fun findDiscountCoupon(userId: Long, couponId: Long, groupId: Long?): BillingClientResources.Reply.CouponDetail

    fun findAvailableCard(userId: Long): BillingClientResources.Reply.CardDetail
}
