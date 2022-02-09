package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

sealed interface PayWay {
    val payment: Payment

    fun addAllBookingToBePay(bookings: List<Booking>) {
        bookings.forEach {
            it.setBy(payment)
        }
    }

    fun pay(
        amountOfPay: Long,
        orderSheet: OrderSheet,
        adjustmentId: Long
    )

    fun repay(
        amountOfRepay: Long,
        adjustmentId: Long?
    )

    fun cancel(cause: String)
    fun cancelWithPenalty(cause: String, amountOfPenalty: Long)
    fun cancelPartial(cause: String, partialCancelAmount: Long)
    fun cancelPartialWithPenalty(cause: String, partialCancelAmount: Long, amountOfPenalty: Long)

    enum class Type {
        POST_PAY,
        PRE_PAY,
        FREE,
        VOUCHER,
    }
}
