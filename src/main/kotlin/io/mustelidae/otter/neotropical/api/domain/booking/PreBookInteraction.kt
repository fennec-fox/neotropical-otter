package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.common.method.pay.UsingPayMethod
import io.mustelidae.otter.neotropical.api.config.CommunicationException
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.order.repository.OrderSheetRepository
import io.mustelidae.otter.neotropical.api.domain.payment.PayWay
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class PreBookInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val orderSheetFinder: OrderSheetFinder,
    private val orderSheetRepository: OrderSheetRepository
) {

    fun book(orderSheet: OrderSheet, usingPayMethod: UsingPayMethod, adjustmentId: Long?): List<Booking> {
        orderSheet.run {
            setUsingPayMethod(usingPayMethod)
            availableOrThrow()
        }

        val priceOfOrder = orderSheet.getPriceOfOrder()

        TODO()
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

    private fun rollbackProgressPayment(payWay: PayWay) {
        return try {
            payWay.cancel("Booking not possible due to service issue")
        } catch (e: Exception) {
            val causeMap = mutableMapOf(
                "paymentId" to payWay.payment.paymentId!!,
                "message" to e.message
            )
            if (e is CommunicationException) {
                causeMap["pgErrorCode"] = e.error.refCode
                causeMap["pgErrorMessage"] = e.error.message
            }
            TODO("send to error message")
        }
    }
}
