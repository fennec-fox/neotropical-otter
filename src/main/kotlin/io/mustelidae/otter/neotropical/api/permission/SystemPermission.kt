package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

class SystemPermission(
    private val id: String
) : Permission {
    private var valid: Boolean = true

    override fun checkOrderSheet(orderSheet: OrderSheet): Boolean {
        val isOk = (id == orderSheet.productCode.id)
        valid = isOk && valid
        return isOk
    }

    override fun checkBooking(booking: Booking): Boolean {
        val isOk = (id == booking.productCode.id)
        valid = isOk && valid
        return isOk
    }

    override fun isValid(): Boolean = valid
}
