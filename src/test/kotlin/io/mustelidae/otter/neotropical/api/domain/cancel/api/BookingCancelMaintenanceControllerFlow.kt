package io.mustelidae.otter.neotropical.api.domain.cancel.api

import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete


internal class BookingCancelMaintenanceControllerFlow(
    private val mockMvc: MockMvc
) {

    fun cancel(userId: Long, bookingIds: List<Long>, cancelFee: Long) {
        val uri = linkTo<BookingCancelMaintenanceController> { cancel(bookingIds, userId, cancelFee, "test") }.toUri()

        mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            header(RoleHeader.XAdmin.KEY, 1234)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }
}