package io.mustelidae.otter.neotropical.api.domain.cancel

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.vertical.CancellationUnit
import io.mustelidae.otter.neotropical.api.domain.vertical.NormalBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import io.mustelidae.otter.neotropical.api.domain.vertical.client.DoNotingClient
import io.mustelidae.otter.neotropical.api.domain.vertical.client.VerticalClient
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.sameOrThrow
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookingCancelInteraction(
    private val bookingFinder: BookingFinder,
    private val verticalClient: VerticalClient,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler,
    private val orderSheetFinder: OrderSheetFinder
) {

    fun cancel(bookingIds: List<Long>, cause: String) {
        val bookings = this.getValidBookings(bookingIds)
        val representativeBooking = bookings.first()

        val callOffBooking = verticalClient.askWhetherCallOff(representativeBooking.userId, bookingIds).apply {
            if (isPossible.not())
                throw PolicyException(Error(ErrorCode.PL03, impossibleReason ?: "Cancellation is not possible."))
        }

        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(representativeBooking.orderId))
        val verticalBooking = verticalHandler.getBooking(orderSheet, bookings)
        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        verticalBooking.cancel(cause)

        if (callOffBooking.hasPenalty())
            payWay.cancelWithPenalty(cause, callOffBooking.cancelFee)
        else
            payWay.cancel(cause)
    }

    fun cancelWithOutCallOff(bookingIds: List<Long>, cancelFee: Long, cause: String) {
        val bookings = this.getValidBookings(bookingIds)
        val representativeBooking = bookings.first()
        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(representativeBooking.orderId))
        val verticalBooking = verticalHandler.getBooking(orderSheet, bookings)
        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        verticalBooking.cancel(cause)

        if (cancelFee != 0L)
            payWay.cancelWithPenalty(cause, cancelFee)
        else
            payWay.cancel(cause)
    }

    fun forceCancelWithoutVerticalShaking(bookingIds: List<Long>, cancelFee: Long, cause: String) {
        val bookings = this.getValidBookings(bookingIds)
        val representativeBooking = bookings.first()
        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(representativeBooking.orderId))

        val verticalBooking = when (representativeBooking.productCode) {
            ProductCode.MOCK_UP -> NormalBooking(DoNotingClient(), orderSheet, bookings)
        }

        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        verticalBooking.cancel(cause)

        if (cancelFee != 0L)
            payWay.cancelWithPenalty(cause, cancelFee)
        else
            payWay.cancel(cause)
    }

    private fun getValidBookings(bookingIds: List<Long>): List<Booking> {
        val bookings = bookingFinder.findAllByIds(bookingIds).apply {
            sameOrThrow()
        }
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)

        val cancelBooks = bookingIds.map { CancellationUnit.CancelBook(it) }
        if (CancelWayCalibration(bookings).isOrderCancel(cancelBooks).not())
            throw PolicyException(Error(ErrorCode.PL04, "Full cancellation is not possible."))

        return bookings
    }
}
