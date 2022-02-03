package io.mustelidae.otter.neotropical.api.domain.booking

interface BookingMaker {
    fun make(): List<Booking>
}
