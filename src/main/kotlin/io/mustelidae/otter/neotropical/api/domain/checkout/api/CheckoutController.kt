package io.mustelidae.otter.neotropical.api.domain.checkout.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.checkout.CheckoutInteraction
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Checkout")
@RestController
@RequestMapping("/v1/checkout")
class CheckoutController(
    private val checkoutInteraction: CheckoutInteraction
) {

    @Operation(description = "create order form")
    @PostMapping("/product/{productCode}/topic/{topicId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun checkout(
        @PathVariable productCode: ProductCode,
        @PathVariable topicId: String,
        @RequestBody request: CheckoutResources.Request.Checkout
    ): Reply<String> {
        return checkoutInteraction.checkout(productCode, topicId, request)
            .toString()
            .toReply()
    }
}
