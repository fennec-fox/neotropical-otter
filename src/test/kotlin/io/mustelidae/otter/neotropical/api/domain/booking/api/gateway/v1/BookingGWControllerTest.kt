package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.kotest.matchers.shouldBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.domain.booking.api.PostPayBookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.PrePayBookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePostPayOfCredit
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePrePayOfOnlyCredit
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import org.junit.jupiter.api.Test

internal class BookingGWControllerTest : FlowTestSupport() {

    @Test
    fun activeBookingsOfPreBooking() {
        // Given
        val userId = 327482432L
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
        activeBookings.size shouldBe bookingIds.size
    }

    @Test
    fun activeBookingsOfPostBooking() {
        // Given
        val userId = 374238472432L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfCredit(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        // Then
        val activeBookings = bookingGWFlow.activeBookings()
        activeBookings.size shouldBe bookingIds.size
    }

    @Test
    fun recordBooking() {
    }

    @Test
    fun recordBookingDetail() {
    }
}
