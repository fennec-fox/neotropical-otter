package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReplies
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.PreBookInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.Point
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.mustelidae.otter.neotropical.api.lock.EnableUserLock
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Booking")
@RestController
@RequestMapping("/v1/bookings")
class PrePayBookingController(
    private val orderSheetFinder: OrderSheetFinder,
    private val preBookingInteraction: PreBookInteraction,
    private val bookingFinder: BookingFinder
) {

    @Operation(summary = "Pre Pay Book")
    @PostMapping("pre-pay-book")
    @EnableUserLock
    @ResponseStatus(HttpStatus.CREATED)
    fun book(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestBody request: BookingResources.Request.PrePayBook
    ): Replies<Long> {
        val orderId = ObjectId(request.orderId)
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(orderSheet)

        val usingPayMethod = UsingPayMethod(
            request.payKey?.let { CreditCard(it) },
            request.point?.let { Point(it) },
            request.discountCoupon?.let { DiscountCoupon(it.id, it.groupId) },
            request.voucher?.let { Voucher(it.id, it.groupId) }
        )

        val bookings = preBookingInteraction.book(orderSheet, usingPayMethod)

        return bookings.map { it.id!! }
            .toReplies()
    }

    @Operation(summary = "Complete Pre Pay")
    @Parameter(name = RoleHeader.XSystem.KEY, description = RoleHeader.XSystem.NAME)
    @PutMapping("{bookingIds}/complete")
    fun complete(
        @PathVariable bookingIds: List<Long>
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XSystem).validOrThrow(bookings)
        preBookingInteraction.completed(bookingIds)
        return Unit.toReply()
    }
}
