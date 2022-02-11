package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReplies
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingPage
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.domain.payment.PaymentFinder
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.VoucherFinder
import io.mustelidae.otter.neotropical.api.domain.vertical.VerticalFinder
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Gateway")
@RestController
@RequestMapping("/v1/gateway")
class BookingGWController(
    private val bookingFinder: BookingFinder,
    private val appEnvironment: AppEnvironment,
    private val orderSheetFinder: OrderSheetFinder,
    private val verticalFinder: VerticalFinder,
    private val paymentFinder: PaymentFinder,
    private val voucherFinder: VoucherFinder
) {

    @GetMapping("active-bookings")
    fun activeBooking(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long
    ): Replies<GWActiveBookingResources.Reply.ActiveBooking> {
        val bookings = bookingFinder.findAllActive(userId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)

        return bookings.map {
            val landingPage = LandingPage(it, appEnvironment)
            GWActiveBookingResources.Reply.ActiveBooking.from(it, landingPage)
        }.toReplies()
    }

    @GetMapping("record-bookings")
    fun recordBooking(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestParam limit: Int,
        @RequestParam lastBookingId: Long? = null
    ): Replies<GWRecordBookingResources.Reply.Record> {
        val bookings = bookingFinder.findAllRecord(userId, limit, lastBookingId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)

        return bookings.map {
            val landingPage = LandingPage(it, appEnvironment)
            GWRecordBookingResources.Reply.Record.from(it, landingPage)
        }.toReplies()
    }

    @GetMapping("record-bookings/{bookingId}")
    fun recordBookingDetail(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable bookingId: Long
    ): Reply<GWRecordBookingResources.Reply.RecordDetail> {

        val booking = bookingFinder.findOneWithItem(bookingId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(booking)

        val verticalRecord = verticalFinder.findRecord(booking.productCode, booking.id!!)
        val paidReceipt = paymentFinder.findOneWithPaidReceipt(booking.payment!!.id!!)

        val voucher = booking.payment!!.voucherId?.let {
            voucherFinder.findOne(userId, it)
        }

        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(booking.orderId))
        val landingPage = LandingPage(booking, appEnvironment)

        return GWRecordBookingResources.Reply.RecordDetail.from(
            orderSheet,
            booking,
            verticalRecord,
            landingPage,
            paidReceipt,
            voucher
        ).toReply()
    }
}
