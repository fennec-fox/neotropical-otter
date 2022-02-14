package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.fromJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

internal class BookingMaintenanceControllerFlow(
    private val mockMvc: MockMvc
) {

    private val adminId = 124124145L

    fun findOne(bookingId: Long): BookingResources.Reply.BookingOfOrderedWithItems {
        val uri = linkTo<BookingMaintenanceController> { findOne(bookingId) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XAdmin.KEY, adminId)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<BookingResources.Reply.BookingOfOrderedWithItems>>()
            .content!!
    }

    fun search(
        productCode: ProductCode,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        userId: Long? = null,
        topicId: String? = null,
        bookingIds: List<Long>? = null,
        verticalBookingId: String? = null,
        status: Booking.Status? = null
    ): List<BookingResources.Reply.BookingOfOrdered> {
        val uri = linkTo<BookingMaintenanceController> { search(productCode, startDate, endDate, userId, topicId, bookingIds, verticalBookingId, status) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XAdmin.KEY, adminId)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<BookingResources.Reply.BookingOfOrdered>>()
            .getContent()
            .toList()
    }
}
