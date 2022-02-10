package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1.BookingGWControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import org.junit.jupiter.api.Test

internal class PrePayBookingControllerTest : FlowTestSupport() {

    @Test
    fun prePayBookingByOnlyCreditCard() {
        // Given
        val userId = 34702372L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfOnlyCredit(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        // Then
        val activeBookings = bookingGWFlow.activeBookings()

        activeBookings.asClue {
            it.size shouldBe 1
        }
    }
}
