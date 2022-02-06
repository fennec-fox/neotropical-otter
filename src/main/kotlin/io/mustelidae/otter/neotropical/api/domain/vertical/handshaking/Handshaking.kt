package io.mustelidae.otter.neotropical.api.domain.vertical.handshaking

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.BookingApproval
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult

interface Handshaking {

    fun accept(
        bookingApproval: BookingApproval,
        orderSheet: OrderSheet,
        bookings: List<Booking>
    ): ExchangeResult
}
