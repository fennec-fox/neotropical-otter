package io.mustelidae.otter.neotropical.api.domain.vertical.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReplies
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.domain.cancel.BookingCancelInteraction
import io.mustelidae.otter.neotropical.api.domain.cancel.ItemCancelInteraction
import io.mustelidae.otter.neotropical.api.domain.cancel.OrderCancelInteraction
import io.mustelidae.otter.neotropical.api.lock.EnableUserLock
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Vertical")
@RestController
@RequestMapping("/v1/product/{productCode}")
class VerticalBookingController(
    private val bookingFinder: BookingFinder,
    private val orderCancelInteraction: OrderCancelInteraction,
    private val bookingCancelInteraction: BookingCancelInteraction,
    private val itemCancelInteraction: ItemCancelInteraction
) {

    @Parameter(name = RoleHeader.XSystem.KEY, description = RoleHeader.XSystem.NAME)
    @EnableUserLock
    @DeleteMapping("order/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancel(
        @PathVariable orderId: String,
        @PathVariable productCode: ProductCode,
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByOrderId(ObjectId(orderId))
        DataAuthentication(RoleHeader.XSystem, RoleHeader.XUser).validOrThrow(bookings)
        orderCancelInteraction.forceCancelWithoutVerticalShaking(ObjectId(orderId), cancelFee, cause)
        return Unit.toReply()
    }

    @Parameter(name = RoleHeader.XSystem.KEY, description = RoleHeader.XSystem.NAME)
    @EnableUserLock
    @DeleteMapping("/bookings/{bookingIds}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancel(
        @PathVariable bookingIds: List<Long>,
        @PathVariable productCode: ProductCode,
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XSystem, RoleHeader.XUser).validOrThrow(bookings)
        bookingCancelInteraction.forceCancelWithoutVerticalShaking(bookingIds, cancelFee, cause)
        return Unit.toReply()
    }

    @Parameter(name = RoleHeader.XSystem.KEY, description = RoleHeader.XSystem.NAME)
    @EnableUserLock
    @DeleteMapping("/booking/{bookingId}/items/{itemIds}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelItem(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable productCode: ProductCode,
        @PathVariable bookingId: Long,
        @PathVariable itemIds: List<Long>,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val booking = bookingFinder.findOneWithItem(bookingId)
        DataAuthentication(RoleHeader.XSystem, RoleHeader.XUser).validOrThrow(booking)
        itemCancelInteraction.forceCancelWithoutVerticalShaking(bookingId, itemIds, cause, cancelFee)
        return Unit.toReply()
    }

    @Parameter(name = RoleHeader.XSystem.KEY, description = RoleHeader.XSystem.NAME)
    @GetMapping("/recent-bookings")
    fun recentBooking(
        @PathVariable productCode: ProductCode,
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestParam topicId: String? = null
    ): Replies<BookingResources.Reply.BookingOfOrdered> {
        val bookings = bookingFinder.findRecentUsage(userId, productCode, topicId)
        return bookings.map { BookingResources.Reply.BookingOfOrdered.from(it) }
            .toReplies()
    }
}
