package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.CallOffBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord

class StableMockUpClient : VerticalClient {
    override fun cancel(
        userId: Long,
        bookingIds: List<Long>,
        cause: String
    ): ExchangeResult = ExchangeResult(true)

    override fun cancelByItem(
        userId: Long,
        bookingId: Long,
        itemIds: List<Long>,
        cause: String
    ): ExchangeResult = ExchangeResult(true)

    override fun findRecord(bookingId: Long): VerticalRecord {
        TODO("Not yet implemented")
    }

    override fun obtain(bookings: List<Booking>, orderSheet: OrderSheet): ExchangeResult = ExchangeResult(true)

    override fun askWhetherCallOff(userId: Long, bookingId: Long): CallOffBooking = CallOffBooking(true, 0, 0)
}
