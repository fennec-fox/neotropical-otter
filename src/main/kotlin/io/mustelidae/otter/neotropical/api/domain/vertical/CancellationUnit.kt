package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder

class CancellationUnit(
    val userId: Long,
    val cancelBooks: List<CancelBook>,
) {
    data class CancelBook(
        val bookingId: Long,
        val itemIds: List<Long>? = null
    )

    fun getCancelPrice(bookingFinder: BookingFinder): Long {
        val bookingIds = cancelBooks.map { it.bookingId }
        val bookings = bookingFinder.findAllByIds(bookingIds)
        var price: Long = 0

        for (cancelBook in cancelBooks) {
            val booking = bookings.find { it.id!! == cancelBook.bookingId }!!
            cancelBook.itemIds?.forEach { itemId ->
                booking.items.find { it.id == itemId }?.apply {
                    price += getTotalPrice()
                }
            }
        }
        return price
    }

    fun getBookingIds(): List<Long> {
        return this.cancelBooks.map { it.bookingId }
    }
}
