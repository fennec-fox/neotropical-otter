package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.fromJson
import io.mustelidae.otter.neotropical.utils.toJson
import org.junit.jupiter.api.Test
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

internal class PrePayBookingControllerFlow(
    private val mockMvc: MockMvc
) {

    @Test
    fun preBook(userId: Long, request: BookingResources.Request.PrePayBook): List<Long> {
        val uri = linkTo<PrePayBookingController> { book(userId, request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<Long>>()
            .getContent().toList()
    }

    @Test
    fun complete(bookingIds: List<Long>) {
        val uri = linkTo<PrePayBookingController> { complete(bookingIds) }.toUri()

        mockMvc.put(uri) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }
}
