package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

interface VerticalBooking {
    val adjustmentId: Long
    val bookings: List<Booking>
    val orderSheet: OrderSheet
    val amountOfPay: Long

    fun book(orderInteraction: OrderInteraction): ExchangeResult

    fun cancel(cause: String): ExchangeResult

    fun cancelByItem(cancellationUnit: CancellationUnit, cause: String): ExchangeResult
}
