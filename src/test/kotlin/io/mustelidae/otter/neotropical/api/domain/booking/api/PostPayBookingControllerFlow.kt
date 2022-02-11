package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.fromJson
import io.mustelidae.otter.neotropical.utils.toJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

internal class PostPayBookingControllerFlow(
    private val mockMvc: MockMvc
) {

    fun postBook(
        userId: Long,
        request: BookingResources.Request.PostPayBook
    ): List<Long> {
        val uri = linkTo<PostPayBookingController> { book(userId, request) }.toUri()

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

    fun complete(productCode: ProductCode, bookingIds: List<Long>, request: BookingResources.Request.PostPayCompleteBook?) {
        val uri = linkTo<PostPayBookingController> { complete(bookingIds, request) }.toUri()

        mockMvc.put(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XSystem.KEY, productCode.id)
            accept = MediaType.APPLICATION_JSON
            content = request?.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }
}
