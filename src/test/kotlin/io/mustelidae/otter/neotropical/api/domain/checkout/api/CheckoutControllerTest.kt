package io.mustelidae.otter.neotropical.api.domain.checkout.api

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CheckoutControllerTest : FlowTestSupport() {
    private val userId = 12717582L

    @Test
    fun checkout1Product() {
        // Given
        val flow = CheckoutControllerFlow(ProductCode.MOCK_UP, mockMvc)
        val topicId = ObjectId().toString()
        val request = CheckoutResources.Request.Checkout.aFixtureByOnce(userId)
        // When & Then
        val orderId = flow.checkout(topicId, request)

        orderId.length shouldBe 24
    }

    @Test
    fun checkoutNoPriceError() {
        // Given
        val flow = CheckoutControllerFlow(ProductCode.MOCK_UP, mockMvc)
        val topicId = ObjectId().toString()
        val request = CheckoutResources.Request.Checkout(
            userId,
            listOf(
                CheckoutResources.Request.Checkout.ProductOrder(
                    "Price",
                    listOf(),
                    listOf(
                        CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                            "Goods Order",
                            1
                        )
                    )
                )
            ),
            1
        )
        // When & Then
        Assertions.assertThrows(java.lang.AssertionError::class.java) {
            flow.checkout(topicId, request)
        }
    }
}
