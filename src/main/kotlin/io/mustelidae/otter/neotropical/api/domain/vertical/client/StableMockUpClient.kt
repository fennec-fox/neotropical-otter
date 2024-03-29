package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.CallOffBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult
import io.mustelidae.otter.neotropical.api.domain.vertical.ObtainResult
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord

class StableMockUpClient : VerticalClient {
    override fun cancel(
        userId: Long,
        bookingIds: List<Long>,
        cause: String
    ): ExchangeResult = ExchangeResult(true)

    override fun cancelByItem(userId: Long, bookingId: Long, itemIds: List<Long>, cause: String): ExchangeResult {
        TODO("Not yet implemented")
    }

    override fun findRecord(bookingId: Long): VerticalRecord {
        TODO("Not yet implemented")
    }

    override fun obtain(bookings: List<Booking>, orderSheet: OrderSheet): ObtainResult {
        TODO("Not yet implemented")
    }

    override fun askBookCallOff(userId: Long, bookingIds: List<Long>): CallOffBooking {
        TODO("Not yet implemented")
    }

    override fun askItemCallOff(userId: Long, bookingId: Long, items: List<Long>): CallOffBooking {
        TODO("Not yet implemented")
    }
}
