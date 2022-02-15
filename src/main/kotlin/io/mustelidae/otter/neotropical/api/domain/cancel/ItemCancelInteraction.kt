package io.mustelidae.otter.neotropical.api.domain.cancel

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("DuplicatedCode")
@Service
@Transactional
class ItemCancelInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler,
    private val orderSheetFinder: OrderSheetFinder,
) {

    fun cancel(bookingId: Long, items: List<Long>, cause: String) {
        val booking = bookingFinder.findOneWithItem(bookingId)
        val userId = booking.userId
        val orderId = ObjectId(booking.orderId)
        val verticalClient = verticalHandler.getClient(booking.productCode)
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBooking(orderSheet, listOf(booking))

        val callOfItem = verticalClient.askItemCallOff(userId, bookingId, items)
        if (callOfItem.isPossible.not())
            throw PolicyException(Error(ErrorCode.PL03, callOfItem.impossibleReason ?: "Cancellation is not possible."))

        verticalBooking.cancelByItem(bookingId, items, cause)

        val partialCancelAmount = booking.items
            .filter { items.contains(it.id!!) }
            .sumOf { it.getTotalPrice() }

        val cancelFee = callOfItem.cancelFee
        val payWay = payWayHandler.getPayWay(booking.payment!!)

        if (cancelFee != 0L)
            payWay.cancelPartialWithPenalty(cause, partialCancelAmount, cancelFee)
        else
            payWay.cancelPartial(cause, partialCancelAmount)

        bookingRepository.save(booking)
    }

    fun cancelWithOutCallOff(bookingId: Long, items: List<Long>, cause: String, cancelFee: Long) {
        val booking = bookingFinder.findOneWithItem(bookingId)
        val orderId = ObjectId(booking.orderId)

        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBooking(orderSheet, listOf(booking))

        verticalBooking.cancelByItem(bookingId, items, cause)

        val partialCancelAmount = booking.items
            .filter { items.contains(it.id!!) }
            .sumOf { it.getTotalPrice() }

        val payWay = payWayHandler.getPayWay(booking.payment!!)

        if (cancelFee != 0L)
            payWay.cancelPartialWithPenalty(cause, partialCancelAmount, cancelFee)
        else
            payWay.cancelPartial(cause, partialCancelAmount)

        bookingRepository.save(booking)
    }

    fun forceCancelWithoutVerticalShaking(bookingId: Long, items: List<Long>, cause: String, cancelFee: Long) {
        val booking = bookingFinder.findOneWithItem(bookingId)
        val orderId = ObjectId(booking.orderId)

        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBookingUseDoNotingVerticalClient(orderSheet, listOf(booking))
        verticalBooking.cancelByItem(bookingId, items, cause)

        val partialCancelAmount = booking.items
            .filter { items.contains(it.id!!) }
            .sumOf { it.getTotalPrice() }

        val payWay = payWayHandler.getPayWay(booking.payment!!)
        if (cancelFee != 0L)
            payWay.cancelPartialWithPenalty(cause, partialCancelAmount, cancelFee)
        else
            payWay.cancelPartial(cause, partialCancelAmount)

        bookingRepository.save(booking)
    }
}
