package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

interface BookingApproval {

    fun obtain(bookings: List<Booking>, orderSheet: OrderSheet): ExchangeResult
}
