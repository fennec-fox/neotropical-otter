package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.DividingQuantityBookingMaker
import io.mustelidae.otter.neotropical.api.domain.booking.Item
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.client.VerticalClient
import io.mustelidae.otter.neotropical.api.domain.vertical.handshaking.OneWayHandshaking
import java.time.LocalDateTime

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
        this.adjustmentId = bookings.first().payment!!.adjustmentId!!
    }

    override val adjustmentId: Long
    override val bookings: List<Booking>
    override val amountOfPay: Long
    override val orderSheet: OrderSheet
    private val verticalClient: VerticalClient

    override fun book(orderInteraction: OrderInteraction): ExchangeResult {
        val exchangeResult = OneWayHandshaking(orderInteraction).accept(verticalClient, orderSheet, bookings)

        exchangeResult.pairingIds?.let { pairs ->
            for (pair in pairs) {
                bookings.find { it.id!! == pair.bookingId }!!.apply {
                    verticalId = pair.verticalId
                }
            }
        }

        exchangeResult.policyCards?.let {
            orderSheet.capture(LocalDateTime.now(), it)
        }

        return exchangeResult
    }

    override fun cancel(cause: String): ExchangeResult {
        for (booking in bookings) {
            val hasNoneTarget = booking.items.none { it.status == Item.Status.ORDERED }
            if (hasNoneTarget)
                throw PolicyException(Error(ErrorCode.PP02, "There is no target to cancel."))

            booking.cancel()
        }
        return verticalClient.cancel(bookings.first().userId, bookings.map { it.id!! }, cause)
    }

    override fun cancelByItem(cancellationUnit: CancellationUnit, cause: String): ExchangeResult {
        for (cancelBook in cancellationUnit.cancelBooks) {
            val booking = bookings.find { it.id!! == cancelBook.bookingId }!!
            cancelBook.itemIds?.forEach { itemId ->
                booking.items.find { it.id == itemId }!!.apply {
                    cancel()
                }
            }
        }

        return verticalClient.cancelByItem(cancellationUnit, cause)
    }
}
