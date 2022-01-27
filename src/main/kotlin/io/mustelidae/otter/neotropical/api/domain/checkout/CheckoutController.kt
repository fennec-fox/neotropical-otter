package io.mustelidae.otter.neotropical.api.domain.checkout

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Checkout")
@RequestMapping("/v1/checkout/product/{productCode}/topic/{topicId}")
class CheckoutController {

    fun checkout(
        @PathVariable productCode: ProductCode,
        @PathVariable topicId: String,
        @RequestBody request: CheckoutResources.Request.Checkout
    ): String {
        TODO("Not yet implemented")
    }
}
