package io.mustelidae.otter.neotropical.api.domain.vertical.handshaking

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.BookingApproval
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult

class OneWayHandshaking(
    private val orderInteraction: OrderInteraction
) : Handshaking {
    override fun accept(
        bookingApproval: BookingApproval,
        orderSheet: OrderSheet,
        bookings: List<Booking>
    ): ExchangeResult {
        val exchangeResult = bookingApproval.obtain(bookings, orderSheet)

        // It should not be rolled back because it is a failure on the part of the order.
        if (exchangeResult.isSuccess) {
            try {
                orderInteraction.complete(orderSheet.id)
            } catch (e: Exception) {
                TODO("If you want to send an error message, implement this part.")
            }
        } else {
            try {
                orderInteraction.fail(orderSheet.id)
            } catch (e: Exception) {
                TODO("If you want to send an error message, implement this part.")
            }
        }

        return exchangeResult
    }
}
