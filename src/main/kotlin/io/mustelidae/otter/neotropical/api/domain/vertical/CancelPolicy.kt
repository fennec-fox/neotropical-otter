package io.mustelidae.otter.neotropical.api.domain.vertical

interface CancelPolicy {

    fun askWhetherCallOff(userId: Long, bookingId: Long): CallOffBooking
}
