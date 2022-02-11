package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.swagger.v3.oas.annotations.media.Schema

class BookingResources {

    class Request {
        @Schema(name = "Booking.Request.PrePayBook")
        data class PrePayBook(
            val orderId: String,
            val payKey: String? = null,
            val point: Long? = null,
            val discountCoupon: Coupon? = null,
            val voucher: Voucher? = null,
            val adjustmentId: Long? = null
        )

        @Schema(name = "Booking.Request.PostPayBook")
        data class PostPayBook(
            val orderId: String,
            val usingPoint: Boolean,
            val creditCard: CreditCard,
            val discountCoupon: DiscountCoupon? = null,
            val voucher: Voucher? = null,
            val adjustmentId: Long? = null
        )

        @Schema(name = "Booking.Request.Voucher")
        data class Voucher(
            val id: Long,
            val groupId: Long? = null
        )

        @Schema(name = "Booking.Request.Coupon")
        data class Coupon(
            val id: Long,
            val groupId: Long? = null
        )

        companion object
    }
}
