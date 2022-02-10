package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.fromJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

internal class BookingGWControllerFlow(
    private val mockMvc: MockMvc,
    private val userId: Long
) {

    fun activeBookings(): List<GWActiveBookingResources.Reply.ActiveBooking> {
        val uri = linkTo<BookingGWController> { activeBooking(userId) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<GWActiveBookingResources.Reply.ActiveBooking>>()
            .getContent()
            .toList()
    }

    fun recordBookings(): List<GWRecordBookingResources.Reply.Record> {

        val uri = linkTo<BookingGWController> { recordBooking(userId, 10) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<GWRecordBookingResources.Reply.Record>>()
            .getContent()
            .toList()
    }

    fun recordDetail(bookingId: Long): GWRecordBookingResources.Reply.RecordDetail {
        val uri = linkTo<BookingGWController> { recordBookingDetail(userId, bookingId) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<GWRecordBookingResources.Reply.RecordDetail>>()
            .content!!
    }
}
