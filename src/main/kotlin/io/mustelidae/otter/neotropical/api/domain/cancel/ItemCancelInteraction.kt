package io.mustelidae.otter.neotropical.api.domain.cancel

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.payment.PayWayHandler
import io.mustelidae.otter.neotropical.api.domain.vertical.CallOffBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.CancellationUnit
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalHandler
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ItemCancelInteraction(
    private val bookingFinder: BookingFinder,
    private val bookingRepository: BookingRepository,
    private val verticalHandler: VerticalHandler,
    private val payWayHandler: PayWayHandler,
    private val orderSheetFinder: OrderSheetFinder,
) {

    fun cancel(cancellationUnit: CancellationUnit, cause: String) {
        val bookingIds = cancellationUnit.cancelBooks.map { it.bookingId }
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)
        val userId = cancellationUnit.userId
        val representativeBooking = bookings.first()
        val productCode = representativeBooking.productCode
        val orderId = ObjectId(representativeBooking.orderId)

        val verticalClient = verticalHandler.getClient(productCode)

        val callOfItems: MutableList<CallOffBooking> = mutableListOf()
        for(cancelBook in cancellationUnit.cancelBooks){
            val callOfItem = verticalClient.askItemCallOff(userId, cancelBook.bookingId, cancelBook.itemIds!!)
            if(callOfItem.isPossible.not())
                throw PolicyException(Error(ErrorCode.PL03, callOfItem.impossibleReason ?: "Cancellation is not possible."))

            callOfItems.add(callOfItem)
        }

        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        val verticalBooking = verticalHandler.getBooking(orderSheet, bookings)

        verticalBooking.cancelByItem(cancellationUnit, cause)
        val partialCancelAmount = getCancelPriceOfItems(bookings, cancellationUnit.cancelBooks)
        val cancelFee = callOfItems.sumOf { it.cancelFee }
        val payWay = payWayHandler.getPayWay(representativeBooking.payment!!)
        if(cancelFee != 0L)
            payWay.cancelPartialWithPenalty(cause, partialCancelAmount, cancelFee)
        else
            payWay.cancelPartial(cause, partialCancelAmount)

        bookingRepository.saveAll(bookings)
    }

    private fun getCancelPriceOfItems(bookings:List<Booking>, cancelBooks: List<CancellationUnit.CancelBook>): Long {
        var price: Long = 0
        for (cancelBook in cancelBooks) {
            val booking = bookings.find { it.id!! == cancelBook.bookingId }!!
            cancelBook.itemIds?.forEach { itemId ->
                booking.items.find { it.id == itemId }!!.apply {
                    price += getTotalPrice()
                }
            }
        }

        return price
    }


}