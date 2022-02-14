package io.mustelidae.otter.neotropical.api.domain.cancel

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import org.bson.types.ObjectId

class CancelWayCalibration(
    orderId: ObjectId,
    bookingFinder: BookingFinder
) {
    private val bookingsOfOrderId: List<Booking> = bookingFinder.findAllByOrderId(orderId)

    fun isOrderCancel(bookingIds: List<Long>): Boolean {
        val canceledBook = bookingsOfOrderId.filter { it.status == Booking.Status.CANCELED }

        if (canceledBook.isNotEmpty())
            return false

        val source = bookingsOfOrderId.map { it.id!! }.sorted()
        val target = bookingIds.sorted()

        if (target.containsAll(source))
            return true

        return false
    }
}
