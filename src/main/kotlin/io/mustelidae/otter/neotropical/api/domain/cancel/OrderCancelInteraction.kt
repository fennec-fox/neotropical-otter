package io.mustelidae.otter.neotropical.api.domain.cancel

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Suppress("DuplicatedCode")
@Service
@Transactional
class OrderCancelInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler,
    private val orderSheetFinder: OrderSheetFinder
) {

    fun cancel(orderId: ObjectId, cause: String) {
        val bookings = bookingFinder.findAllByOrderId(orderId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)

        val representativeBooking = bookings.first()
        val verticalClient = verticalHandler.getClient(representativeBooking.productCode)

        val callOffBooking = verticalClient.askBookCallOff(representativeBooking.userId, bookings.map { it.id!! }).apply {
            if (isPossible.not())
                throw PolicyException(Error(ErrorCode.PL03, impossibleReason ?: "Cancellation is not possible."))
        }

        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBooking(orderSheet, bookings)
        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        verticalBooking.cancel(cause)

        if (callOffBooking.hasPenalty())
            payWay.cancelWithPenalty(cause, callOffBooking.cancelFee)
        else
            payWay.cancel(cause)

        bookingRepository.saveAll(bookings)
    }

    fun cancelWithOutCallOff(orderId: ObjectId, cancelFee: Long, cause: String) {
        val bookings = bookingFinder.findAllByOrderId(orderId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)
        val representativeBooking = bookings.first()
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBooking(orderSheet, bookings)
        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        verticalBooking.cancel(cause)

        if (cancelFee != 0L)
            payWay.cancelWithPenalty(cause, cancelFee)
        else
            payWay.cancel(cause)

        bookingRepository.saveAll(bookings)
    }

    fun forceCancelWithoutVerticalShaking(orderId: ObjectId, cancelFee: Long, cause: String) {
        val bookings = bookingFinder.findAllByOrderId(orderId)
        val representativeBooking = bookings.first()
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBookingUseDoNotingVerticalClient(orderSheet, bookings)

        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        verticalBooking.cancel(cause)

        if (cancelFee != 0L)
            payWay.cancelWithPenalty(cause, cancelFee)
        else
            payWay.cancel(cause)

        bookingRepository.saveAll(bookings)
    }
}
