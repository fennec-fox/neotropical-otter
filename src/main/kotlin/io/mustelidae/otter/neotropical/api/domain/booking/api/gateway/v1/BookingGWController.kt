package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReplies
import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingPage
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/gateway")
class BookingGWController(
    private val bookingFinder: BookingFinder,
    private val appEnvironment: AppEnvironment
) {

    @GetMapping("active-bookings")
    fun activeBooking(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long
    ): Replies<GWActiveBookingResources.Reply.ActiveBooking> {
        val bookings = bookingFinder.findAllActive(userId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(bookings)

        return bookings.map {
            val landingPage = LandingPage(it.productCode, it.topicId, appEnvironment)
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
            val landingPage = LandingPage(it.productCode, it.topicId, appEnvironment)
            GWRecordBookingResources.Reply.Record.from(it, landingPage)
        }.toReplies()
    }

    @GetMapping("record-bookings/{bookingId}")
    fun recordBookingDetail(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable bookingId: Long
    ): Reply<GWRecordBookingResources.Reply.RecordDetail> {
        TODO()
    }
}
