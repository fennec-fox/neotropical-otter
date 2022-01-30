package io.mustelidae.otter.neotropical.api.domain.order.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.fromJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

class OrderControllerFlow(
    private val productCode: ProductCode,
    private val mockMvc: MockMvc
) {

    fun findOrder(userId: Long, orderId: String): OrderResources.Reply.PurchaseOrder {
        val uri = linkTo<OrderController> { findOne(userId, productCode, orderId) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
            header(RoleHeader.XUser.KEY, userId)
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<OrderResources.Reply.PurchaseOrder>>()
            .content!!
    }
}
