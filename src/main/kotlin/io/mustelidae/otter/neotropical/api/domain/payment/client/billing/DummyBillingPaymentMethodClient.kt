package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import java.time.LocalDateTime
import kotlin.random.Random

class DummyBillingPaymentMethodClient : BillingPaymentMethodClient {
    override fun findCard(userId: Long, payKey: String): BillingClientResources.Reply.CardDetail {
        return BillingClientResources.Reply.CardDetail(
            Random.nextLong(),
            payKey,
            "American express",
            "1234-****-1234-**34",
            "VISA",
            true,
            true,
            "My First Card"
        )
    }

    override fun findDiscountCoupon(
        userId: Long,
        couponId: Long,
        groupId: Long?
    ): BillingClientResources.Reply.CouponDetail {
        return BillingClientResources.Reply.CouponDetail(
            couponId,
            "Dummy Discount Coupon",
            500,
            true,
            "I use it only for development",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(7)
        )
    }
}
