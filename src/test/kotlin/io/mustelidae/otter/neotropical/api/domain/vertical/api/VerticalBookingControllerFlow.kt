package io.mustelidae.otter.neotropical.api.domain.vertical.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.fromJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get

internal class VerticalBookingControllerFlow(
    private val mockMvc: MockMvc
) {

    fun cancelAll(userId: Long, orderId: String, productCode: ProductCode) {
        val uri = linkTo<VerticalBookingController> { cancel(orderId, productCode, userId, 100L, "test") }.toUri()

        mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            header(RoleHeader.XSystem.KEY, productCode.id)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    fun cancelBooking(bookingIds: List<Long>, productCode: ProductCode, userId: Long) {
        val uri = linkTo<VerticalBookingController> { cancel(bookingIds, productCode, userId, 0, "test") }.toUri()

        mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            header(RoleHeader.XSystem.KEY, productCode.id)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    fun recentBooking(productCode: ProductCode, userId: Long): List<BookingResources.Reply.BookingOfOrdered> {
        val uri = linkTo<VerticalBookingController> { recentBooking(productCode, userId) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            accept = MediaType.APPLICATION_JSON
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
