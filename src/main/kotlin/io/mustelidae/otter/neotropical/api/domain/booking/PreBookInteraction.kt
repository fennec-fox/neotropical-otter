package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.common.method.pay.UsingPayMethod
import io.mustelidae.otter.neotropical.api.config.CommunicationException
import io.mustelidae.otter.neotropical.api.config.HandshakeFailException
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.order.repository.OrderSheetRepository
import io.mustelidae.otter.neotropical.api.domain.payment.PayWay
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class PreBookInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val orderInteraction: OrderInteraction,
    private val orderSheetFinder: OrderSheetFinder,
    private val orderSheetRepository: OrderSheetRepository,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler
) {

    fun book(orderSheet: OrderSheet, usingPayMethod: UsingPayMethod, adjustmentId: Long?): List<Booking> {
        orderSheet.run {
            setUsingPayMethod(usingPayMethod)
            availableOrThrow()
        }
        val userId = orderSheet.userId
        val verticalBooking = verticalHandler.getBooking(orderSheet)
        val adjustmentId = verticalBooking.adjustmentId
        val amountOfPay = verticalBooking.amountOfPay

        val payWay = payWayHandler.getPrePayWay(userId, amountOfPay, usingPayMethod.voucher).apply {
            addAllBookingToBePay(verticalBooking.bookings)
        }

        payWay.pay(amountOfPay, orderSheet, adjustmentId)

        bookingRepository.saveAll(verticalBooking.bookings)

        val exchangeResult = verticalBooking.book(orderInteraction)

        if (exchangeResult.isSuccess.not()) {
            rollbackProgressPayment(userId, payWay)
        }

        return verticalBooking.bookings
    }

    fun completed(bookingIds: List<Long>, policyCards: List<PolicyCard>) {
        val bookings = bookingFinder.findIn(bookingIds)
        for (booking in bookings) {
            booking.completed()

            val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(booking.orderId))
            orderSheet.capture(LocalDateTime.now(), policyCards)
            orderSheetRepository.save(orderSheet)
        }
        bookingRepository.saveAll(bookings)
    }

    private fun rollbackProgressPayment(userId: Long, payWay: PayWay) {
        return try {
            payWay.cancel("Booking not possible due to service issue")
        } catch (e: Exception) {
            if (e is CommunicationException)
                throw HandshakeFailException(userId, payWay.payment, e)
            else
                throw HandshakeFailException(userId, payWay.payment, e.message)
        }
    }
}
