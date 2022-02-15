package io.mustelidae.otter.neotropical.api.domain.cancel.api

import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Cancel")
@RestController
@RequestMapping("/v1/maintenance/cancel")
class BookingCancelMaintenanceController(
    private val bookingFinder: BookingFinder,
    private val orderCancelInteraction: OrderCancelInteraction,
    private val bookingCancelInteraction: BookingCancelInteraction,
    private val itemCancelInteraction: ItemCancelInteraction
) {

    @Parameter(name = RoleHeader.XAdmin.KEY, description = RoleHeader.XAdmin.NAME)
    @EnableUserLock
    @DeleteMapping("order/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancel(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable id: String,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val orderId = ObjectId(id)
        val bookings = bookingFinder.findAllByOrderId(orderId)
        DataAuthentication(RoleHeader.XAdmin, RoleHeader.XUser).validOrThrow(bookings)
        orderCancelInteraction.cancelWithOutCallOff(orderId, cancelFee, cause)
        return Unit.toReply()
    }

    @Parameter(name = RoleHeader.XAdmin.KEY, description = RoleHeader.XAdmin.NAME)
    @EnableUserLock
    @DeleteMapping("bookings/{bookingIds}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancel(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable bookingIds: List<Long>,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XAdmin, RoleHeader.XUser).validOrThrow(bookings)
        bookingCancelInteraction.cancelWithOutCallOff(bookingIds, cancelFee, cause)
        return Unit.toReply()
    }

    @Parameter(name = RoleHeader.XAdmin.KEY, description = RoleHeader.XAdmin.NAME)
    @EnableUserLock
    @DeleteMapping("/booking/{bookingId}/items/{itemIds}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelItem(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable bookingId: Long,
        @PathVariable itemIds: List<Long>,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val booking = bookingFinder.findOneWithItem(bookingId)
        DataAuthentication(RoleHeader.XAdmin, RoleHeader.XUser).validOrThrow(booking)
        itemCancelInteraction.cancelWithOutCallOff(bookingId, itemIds, cause, cancelFee)
        return Unit.toReply()
    }
}
