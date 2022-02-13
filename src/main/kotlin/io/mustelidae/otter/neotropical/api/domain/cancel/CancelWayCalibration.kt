package io.mustelidae.otter.neotropical.api.domain.cancel

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.Item
import io.mustelidae.otter.neotropical.api.domain.vertical.CancellationUnit

class CancelWayCalibration(
    private val bookings: List<Booking>
) {

    fun isOrderCancel(cancelBooks: List<CancellationUnit.CancelBook>): Boolean {
        for (cancelBook in cancelBooks) {
            cancelBook.itemIds?.let {
                if (isBookingCancel(cancelBook).not())
                    return false
            }
        }

        val source = bookings.map { it.id!! }.sorted()
        val target = cancelBooks.map { it.bookingId }.sorted()

        return (source.containsAll(target))
    }

    private fun isBookingCancel(cancelBook: CancellationUnit.CancelBook): Boolean {
        val booking = bookings.find { it.id == cancelBook.bookingId }!!

        if (booking.status == Booking.Status.CANCELED)
            return false

        for (item in booking.items) {
            if (item.status == Item.Status.CANCELED)
                return false
        }

        val source = booking.items.map { it.id!! }.sorted()
        val target = cancelBook.itemIds!!.sorted()

        return (source.containsAll(target))
    }
}
