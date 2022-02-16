package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookingInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
) {

    fun confirm(bookingIds: List<Long>) {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        bookings.forEach {
            it.book()
        }
        bookingRepository.saveAll(bookings)
    }
}
