package io.mustelidae.otter.neotropical.api.domain.vertical.handshaking

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.vertical.BookingApproval
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult

class ThreeWayHandshaking(
    private val orderSheetFinder: OrderSheetFinder
) : Handshaking {
    override fun accept(
        bookingApproval: BookingApproval,
        orderSheet: OrderSheet,
        bookings: List<Booking>
    ): ExchangeResult {

        val exchangeResult = bookingApproval.obtain(bookings, orderSheet)

        if (exchangeResult.isSuccess.not())
            return exchangeResult

        val orderOfAck = orderSheetFinder.findOne(orderSheet.id)!!

        if (orderOfAck.status != OrderSheet.Status.ORDERED)
            return ExchangeResult(
                false,
                """
                The reservation approval request was processed normally. 
                However, it fails because the ACK is abnormal.
                """.trimIndent()
            )

        return exchangeResult
    }
}
