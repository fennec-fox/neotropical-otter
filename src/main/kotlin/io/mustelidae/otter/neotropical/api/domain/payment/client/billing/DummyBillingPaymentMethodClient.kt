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
            isPrimary = true,
            status = true,
            nickName = "My First Card"
        )
    }

    override fun findDiscountCoupon(
        userId: Long,
        couponId: Long,
        groupId: Long?
    ): BillingClientResources.Reply.CouponDetail {
        if (couponId == 2L)
            return couponOf1000

        if (couponId == 3L)
            return couponOf10000

        if (couponId == 4L)
            return couponOfUsed

        return couponOf500
    }

    override fun findAvailableCard(userId: Long): BillingClientResources.Reply.CardDetail {
        return BillingClientResources.Reply.CardDetail(
            Random.nextLong(),
            "3oafhap38yraow837ad",
            "Global ",
            "1464-****-6824-**34",
            "MASTER",
            isPrimary = false,
            status = true,
            nickName = "My First Card"
        )
    }

    companion object {
        val couponOf500 = BillingClientResources.Reply.CouponDetail(
            1,
            "Dummy Discount Coupon",
            500,
            true,
            "I use it only for development",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7)
        )

        val couponOf1000 = BillingClientResources.Reply.CouponDetail(
            2,
            "Dummy Discount Coupon",
            1000,
            true,
            "I use it only for development",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7)
        )

        val couponOf10000 = BillingClientResources.Reply.CouponDetail(
            3,
            "Dummy Discount Coupon",
            10000,
            true,
            "I use it only for development",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7)
        )

        val couponOfUsed = BillingClientResources.Reply.CouponDetail(
            3,
            "Dummy Discount Coupon",
            10000,
            false,
            "I use it only for development",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7)
        )
    }
}
