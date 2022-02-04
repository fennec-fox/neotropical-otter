package io.mustelidae.otter.neotropical.api.domain.vertical

interface CancelPolicy {

    fun checkWhetherCallOff(userId: Long, bookingId: Long): CallOffBooking
}