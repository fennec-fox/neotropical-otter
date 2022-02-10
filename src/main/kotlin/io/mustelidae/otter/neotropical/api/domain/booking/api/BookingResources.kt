package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher

class BookingResources {

    class Request {

        data class PrePayBook(
            val orderId: String,
            val payKey: String? = null,
            val point: Long? = null,
            val discountCoupon: Coupon? = null,
            val voucher: Voucher? = null,
            val adjustmentId: Long? = null
        )

        data class PostPayBook(
            val orderId: String,
            val usingPoint: Boolean,
            val creditCard: CreditCard,
            val discountCoupon: DiscountCoupon? = null,
            val voucher: Voucher? = null,
            val adjustmentId: Long? = null
        )

        data class Voucher(
            val id: Long,
            val groupId: Long? = null
        )

        data class Coupon(
            val id: Long,
            val groupId: Long? = null
        )

        companion object
    }
}
