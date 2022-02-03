package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.method.pay.CreditCard
import io.mustelidae.otter.neotropical.api.common.method.pay.DiscountCoupon
import io.mustelidae.otter.neotropical.api.common.method.pay.Point
import io.mustelidae.otter.neotropical.api.common.method.pay.Voucher

class BookingResources {

    class Request {

        data class PrePayBook(
            val orderId: String,
            val creditCard: CreditCard? = null,
            val point: Point? = null,
            val discountCoupon: DiscountCoupon? = null,
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
    }
}
