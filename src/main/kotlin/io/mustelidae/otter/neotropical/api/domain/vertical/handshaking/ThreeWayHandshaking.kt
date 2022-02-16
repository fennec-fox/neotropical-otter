package io.mustelidae.otter.neotropical.api.domain.vertical.handshaking

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.vertical.BookingApproval
import io.mustelidae.otter.neotropical.api.domain.vertical.ObtainResult

class ThreeWayHandshaking(
    private val orderSheetFinder: OrderSheetFinder
) : Handshaking {
    override fun accept(
        bookingApproval: BookingApproval,
        orderSheet: OrderSheet,
        bookings: List<Booking>
    ): ObtainResult {

        val obtainResult: ObtainResult
        try {
            obtainResult = bookingApproval.obtain(bookings, orderSheet)

            if (obtainResult.isSuccess.not())
                return obtainResult
        } catch (e: Exception) {
            TODO("If you want to send an error message, implement this part.")
        }

        val orderOfAck = orderSheetFinder.findOne(orderSheet.id)!!

        if (orderOfAck.status != OrderSheet.Status.ORDERED)
            return ObtainResult(
                isSuccess = false,
                onAutoConfirm = false,
                failCause = """
                                The reservation approval request was processed normally. 
                                However, it fails because the ACK is abnormal.
                """.trimIndent()
            )

        return obtainResult
    }
}
