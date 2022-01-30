package io.mustelidae.otter.neotropical.api.domain.order.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.utils.toISO
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

internal class OrderControllerTest : FlowTestSupport() {
    private val userId = 124267234L

    @Test
    fun find() {
        // Given
        val productCode = ProductCode.MOCK_UP
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val orderFlow = OrderControllerFlow(productCode, mockMvc)

        val topicId = ObjectId().toString()
        val request = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        // When
        val orderId = checkoutFlow.checkout(topicId, request)
        val reply = orderFlow.findOrder(userId, orderId)

        // Then
        reply.asClue {
            it.id shouldBe orderId
            it.policyCards shouldBe null
            it.products.size shouldBe 2
            it.products.first().asClue { p ->
                val r = request.products.first()
                p.id shouldBe r.id
                p.price shouldBe r.price
                p.reservationDate?.toISO() shouldBe r.reservationDate?.toISO()
                p.description shouldBe r.description
            }
            it.status shouldBe OrderSheet.Status.WAIT
        }
    }
}
