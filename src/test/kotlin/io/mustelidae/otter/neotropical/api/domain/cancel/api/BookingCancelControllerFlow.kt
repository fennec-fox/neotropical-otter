package io.mustelidae.otter.neotropical.api.domain.cancel.api

import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete

internal class BookingCancelControllerFlow(
    private val mockMvc: MockMvc
) {

    fun cancel(userId: Long, bookingIds: List<Long>) {
        val uri = linkTo<BookingCancelController> { cancel(userId, bookingIds) }.toUri()

        mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    fun cancel(userId: Long, orderId: String) {
        val uri = linkTo<BookingCancelController> { cancel(userId, orderId, null) }.toUri()

        mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    fun cancel(userId: Long, bookingId: Long, items: List<Long>) {
        val uri = linkTo<BookingCancelController> { cancelItem(userId, bookingId, items) }.toUri()
        mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }
}
