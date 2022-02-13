package io.mustelidae.otter.neotropical.api.domain.vertical

interface CancelPolicy {

    fun askWhetherCallOff(userId: Long, bookingIds: List<Long>): CallOffBooking
}
