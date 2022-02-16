package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.put

internal class BookingControllerFlow(
    private val mockMvc: MockMvc
) {

    fun confirm(productCode: ProductCode, bookingIds: List<Long>,) {
        val uri = linkTo<BookingController> { confirm(bookingIds) }.toUri()

        mockMvc.put(uri) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            header(RoleHeader.XSystem.KEY, productCode.id)
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }
}
