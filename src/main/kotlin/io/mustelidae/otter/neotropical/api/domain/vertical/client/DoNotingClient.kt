package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.CallOffBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult
import io.mustelidae.otter.neotropical.api.domain.vertical.ObtainResult
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord

class DoNotingClient : VerticalClient {
    override fun cancel(userId: Long, bookingIds: List<Long>, cause: String): ExchangeResult {
        return ExchangeResult(true)
    }

    override fun cancelByItem(userId: Long, bookingId: Long, itemIds: List<Long>, cause: String): ExchangeResult {
        return ExchangeResult(true)
    }

    override fun findRecord(bookingId: Long): VerticalRecord {
        throw DevelopMistakeException("This should do nothing.")
    }

    override fun obtain(bookings: List<Booking>, orderSheet: OrderSheet): ObtainResult {
        throw DevelopMistakeException("This should do nothing.")
    }

    override fun askBookCallOff(userId: Long, bookingIds: List<Long>): CallOffBooking {
        throw DevelopMistakeException("This should do nothing.")
    }

    override fun askItemCallOff(userId: Long, bookingId: Long, items: List<Long>): CallOffBooking {
        TODO("Not yet implemented")
    }
}
