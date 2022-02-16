package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.config.CommunicationException
import io.mustelidae.otter.neotropical.api.config.HandshakeFailException
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.PayWay
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import io.mustelidae.otter.neotropical.utils.sameOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PreBookInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val orderInteraction: OrderInteraction,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler,
    private val billingPaymentMethodClient: BillingPaymentMethodClient,
    private val voucherClient: VoucherClient
) {

    fun book(orderSheet: OrderSheet, usingPayMethod: UsingPayMethod): List<Booking> {
        val userId = orderSheet.userId

        usingPayMethod.run {
            fillUpDetailAll(userId, billingPaymentMethodClient, voucherClient)
            validOrThrow()
        }

        orderSheet.run {
            setUsingPayMethod(usingPayMethod)
            availableOrThrow()
        }

        val verticalBooking = verticalHandler.getBooking(orderSheet)
        val adjustmentId = verticalBooking.adjustmentId
        val amountOfPay = verticalBooking.amountOfPay

        val payWay = payWayHandler.getPayWayOfPrePayBook(userId, amountOfPay, usingPayMethod.voucher).apply {
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

    fun completed(bookingIds: List<Long>) {
        val bookings = bookingFinder.findInWithPayment(bookingIds).apply {
            sameOrThrow()
        }

        for (booking in bookings) {
            booking.completed()
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
