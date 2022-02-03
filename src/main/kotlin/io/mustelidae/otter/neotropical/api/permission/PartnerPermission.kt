package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

class PartnerPermission(
    private val id: Long
) : Permission {
    private var valid: Boolean = true
    override fun checkOrderSheet(orderSheet: OrderSheet): Boolean = false
    override fun checkBooking(booking: Booking): Boolean = true

    override fun isValid(): Boolean = valid
}
