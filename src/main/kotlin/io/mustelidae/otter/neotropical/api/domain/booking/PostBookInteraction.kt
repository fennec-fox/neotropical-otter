package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.config.HandshakeFailException
import io.mustelidae.otter.neotropical.api.config.InvalidArgumentException
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.order.repository.OrderSheetRepository
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.payment.PaymentMethodCalibration
import io.mustelidae.otter.neotropical.api.domain.payment.VoucherPayWay
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import io.mustelidae.otter.neotropical.utils.sameOrThrow
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostBookInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val orderInteraction: OrderInteraction,
    private val orderSheetFinder: OrderSheetFinder,
    private val orderSheetRepository: OrderSheetRepository,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler,
    private val paymentMethodCalibration: PaymentMethodCalibration
) {

    fun book(orderSheet: OrderSheet, usingPayMethod: UsingPayMethod): List<Booking> {
        val userId = orderSheet.userId
        usingPayMethod.run {
            if (this.hasCard().not())
                throw InvalidArgumentException("Postpaid orders require a card.")
        }

        orderSheet.run {
            setUsingPayMethod(usingPayMethod)
            availableOrThrow()
        }

        val verticalBooking = verticalHandler.getBooking(orderSheet)
        val amountOfPay = verticalBooking.amountOfPay

        val payWay = payWayHandler.getPayWayOfPostPayBook(userId, amountOfPay, usingPayMethod.voucher).apply {
            addAllBookingToBePay(verticalBooking.bookings)
        }

        if (payWay is VoucherPayWay)
            payWay.reserveUse()

        bookingRepository.saveAll(verticalBooking.bookings)
        val exchangeResult = verticalBooking.book(orderInteraction)

        if (exchangeResult.isSuccess.not()) {
            if (payWay is VoucherPayWay)
                payWay.rollbackReserve()

            throw HandshakeFailException(userId, orderSheet.productCode, exchangeResult.failCause)
        }

        bookingRepository.saveAll(verticalBooking.bookings)
        orderSheetRepository.save(orderSheet)

        return verticalBooking.bookings
    }

    fun completed(
        bookingIds: List<Long>,
        changeAmount: Long? = null,
        changeAdjustmentId: Long? = null,
        cause: String? = null,
        changePaymentMethod: UsingPayMethod? = null
    ) {
        val bookings = bookingFinder.findInWithPayment(bookingIds).apply {
            sameOrThrow()
        }

        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(bookings.first().orderId))
        val userId = orderSheet.userId
        val payment = bookings.first().payment!!
        val amountOfPay = changeAmount ?: payment.priceOfOrder
        val adjustmentId = changeAdjustmentId ?: orderSheet.adjustmentId
        val usingPayMethod = paymentMethodCalibration.calibrate(userId, orderSheet, changePaymentMethod)

        orderSheet.changeUsingPayMethod(usingPayMethod)

        bookings.forEach {
            it.completed()
        }

        val payWay = payWayHandler.getPayWayOfPostPayBook(payment, changeAmount, usingPayMethod.voucher)
        payWay.pay(amountOfPay, orderSheet, adjustmentId)

        bookingRepository.saveAll(bookings)
        orderSheetRepository.save(orderSheet)
    }
}
