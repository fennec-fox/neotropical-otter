package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.CallOffBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.CancellationUnit
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord

class StableMockUpClient : VerticalClient {
    override fun cancel(
        userId: Long,
        bookingIds: List<Long>,
        cause: String
    ): ExchangeResult = ExchangeResult(true)

    override fun cancelByItem(cancellationUnit: CancellationUnit, cause: String): ExchangeResult {
        TODO("Not yet implemented")
    }

    override fun findRecord(bookingId: Long): VerticalRecord {
        TODO("Not yet implemented")
    }

    override fun obtain(bookings: List<Booking>, orderSheet: OrderSheet): ExchangeResult {
        TODO("Not yet implemented")
    }

    override fun askBookCallOff(userId: Long, bookingIds: List<Long>): CallOffBooking {
        TODO("Not yet implemented")
    }

    override fun askItemCallOff(userId: Long, bookingId: Long, items: List<Long>): CallOffBooking {
        TODO("Not yet implemented")
    }
}
