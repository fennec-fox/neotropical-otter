package io.mustelidae.otter.neotropical.api.domain.vertical

interface CancelPolicy {

    fun askBookCallOff(userId: Long, bookingIds: List<Long>): CallOffBooking

    fun askItemCallOff(userId: Long, bookingId: Long, items: List<Long>): CallOffBooking
}
