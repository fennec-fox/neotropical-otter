package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.PreBookInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.lock.EnableUserLock
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/bookings")
class PrePayBookingController(
    private val orderSheetFinder: OrderSheetFinder,
    private val bookingFinder: BookingFinder,
    private val preBookingInteraction: PreBookInteraction
) {

    @PostMapping("pre-pay-book")
    @EnableUserLock
    fun book(
        @RequestHeader userId: Long,
        @RequestBody request: BookingResources.Request.PrePayBook
    ): Replies<Long> {
        val orderId = ObjectId(request.orderId)
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(orderSheet)
        TODO()
    }

    @PutMapping("{bookingId}")
    fun complete(
        @PathVariable bookingId: Long
    ): Reply<Unit> {
        val booking = bookingFinder.findOneWithItem(bookingId)
        DataAuthentication(RoleHeader.XSystem)
        preBookingInteraction.completed(booking)
        return Unit.toReply()
    }
}
