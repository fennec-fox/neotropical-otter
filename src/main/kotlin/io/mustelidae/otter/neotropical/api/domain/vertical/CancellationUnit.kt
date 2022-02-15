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

    }
}
