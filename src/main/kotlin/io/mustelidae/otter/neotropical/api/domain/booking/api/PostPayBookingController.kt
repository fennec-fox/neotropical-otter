package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReplies
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.PostBookInteraction
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
class PostPayBookingController(
    private val orderSheetFinder: OrderSheetFinder,
    private val postBookInteraction: PostBookInteraction,
    private val bookingFinder: BookingFinder
) {

    @Operation(summary = "Post Pay Book")
    @PostMapping("post-pay-book")
    @EnableUserLock
    @ResponseStatus(HttpStatus.CREATED)
    fun book(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestBody request: BookingResources.Request.PostPayBook
    ): Replies<Long> {
        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(request.orderId))
        DataAuthentication(RoleHeader.XUser).validOrThrow(orderSheet)

        val usingPayMethod = UsingPayMethod(
            CreditCard(request.payKey),
            request.point?.let { Point(it) },
            request.discountCoupon?.let { DiscountCoupon(it.id, it.groupId) },
            request.voucher?.let { Voucher(it.id, it.groupId) }
        )

        val bookings = postBookInteraction.book(orderSheet, usingPayMethod)

        return bookings.map { it.id!! }
            .toReplies()
    }

    @Operation(summary = "Complete Post Pay Book")
    @Parameter(name = RoleHeader.XSystem.KEY, description = RoleHeader.XSystem.NAME)
    @PutMapping("{bookingIds}/complete-n-pay")
    fun complete(
        @PathVariable bookingIds: List<Long>,
        @RequestBody request: BookingResources.Request.PostPayCompleteBook? = null
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XSystem).validOrThrow(bookings)

        postBookInteraction.completed(
            bookingIds,
            request?.changeAmount,
            request?.changeAdjustmentId,
            request?.cause,
            request?.changePaymentMethod
        )
        return Unit.toReply()
    }
}
