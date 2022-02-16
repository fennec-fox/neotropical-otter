package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.DividingQuantityBookingMaker
import io.mustelidae.otter.neotropical.api.domain.booking.Item
import io.mustelidae.otter.neotropical.api.domain.order.OrderForm
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.client.VerticalClient
import io.mustelidae.otter.neotropical.api.domain.vertical.handshaking.OneWayHandshaking

class NormalBooking : VerticalBooking {
    constructor(verticalClient: VerticalClient, orderSheet: OrderSheet) {
        val bookings = DividingQuantityBookingMaker(orderSheet).make()
        this.bookings = bookings
        this.orderSheet = orderSheet
        this.amountOfPay = bookings.sumOf { it.getTotalPrice() }
        this.adjustmentId = orderSheet.adjustmentId
        this.verticalClient = verticalClient
    }

    constructor(verticalClient: VerticalClient, orderSheet: OrderSheet, bookings: List<Booking>) {
        this.verticalClient = verticalClient
        this.bookings = bookings
        this.orderSheet = orderSheet
        this.amountOfPay = bookings.sumOf { it.getTotalPrice() }
        this.adjustmentId = bookings.first().payment!!.adjustmentId ?: orderSheet.adjustmentId
    }

    override val adjustmentId: Long
    override val bookings: List<Booking>
    override val amountOfPay: Long
    override val orderSheet: OrderSheet
    private val verticalClient: VerticalClient

    override fun book(orderInteraction: OrderInteraction): ExchangeResult {
        val obtainResult = OneWayHandshaking(orderInteraction).accept(verticalClient, orderSheet, bookings)

        obtainResult.pairingIds?.let { pairs ->
            for (pair in pairs) {
                bookings.find { it.id!! == pair.bookingId }!!.apply {
                    verticalId = pair.verticalId
                }
            }
        }

        obtainResult.policyCards?.let {
            OrderForm(orderSheet).apply {
                stompingPolicy(it)
            }
        }

        if (obtainResult.onAutoConfirm)
            bookings.forEach { it.book() }

        return obtainResult
    }

    override fun cancel(cause: String): ExchangeResult {
        for (booking in bookings) {
            booking.validOrThrow()

            if (booking.items.isNotEmpty()) {
                val hasNoneTarget = booking.items.none { it.status != Item.Status.CANCELED }
                if (hasNoneTarget)
                    throw PolicyException(Error(ErrorCode.PP02, "There is no target to cancel."))
            }

            booking.cancel()
        }
        return verticalClient.cancel(bookings.first().userId, bookings.map { it.id!! }, cause)
    }

    override fun cancelByItem(bookingId: Long, itemIds: List<Long>, cause: String): ExchangeResult {
        val booking = bookings.find { it.id!! == bookingId }!!.apply {
            validOrThrow()
        }

        for (itemId in itemIds) {
            val item = booking.items.find { it.id == itemId }!!
            item.cancel()
        }
        return verticalClient.cancelByItem(booking.userId, bookingId, itemIds, cause)
    }
}

private fun Booking.validOrThrow() {
    if (this.status == Booking.Status.BOOKED)
        throw PolicyException(Error(ErrorCode.PP05, "Cancellation is not possible while in use."))
    if (this.status == Booking.Status.CANCELED)
        throw PolicyException(Error(ErrorCode.PP05, "It has already been cancelled."))
}
