package io.mustelidae.otter.neotropical.api.domain.checkout.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.utils.fromJson
import io.mustelidae.otter.neotropical.utils.toJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

internal class CheckoutControllerFlow(
    private val productCode: ProductCode,
    private val mockMvc: MockMvc
) {
    fun checkout(topicId: String, request: CheckoutResources.Request.Checkout): String {
        val uri = linkTo<CheckoutController> { checkout(productCode, topicId, request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<String>>()
            .content!!
    }
}
