package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PreBookInteraction(
    private val bookingRepository: BookingRepository
) {

    fun completed(booking: Booking) {
        booking.completed()
        bookingRepository.save(booking)
    }
}
