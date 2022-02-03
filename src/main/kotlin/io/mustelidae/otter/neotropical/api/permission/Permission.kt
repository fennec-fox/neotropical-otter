package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

interface Permission {

    fun checkOrderSheet(orderSheet: OrderSheet): Boolean
    fun checkBooking(booking: Booking): Boolean
    fun isValid(): Boolean
}
