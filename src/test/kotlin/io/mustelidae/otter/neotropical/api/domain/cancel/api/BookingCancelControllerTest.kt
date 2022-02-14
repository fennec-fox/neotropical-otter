package io.mustelidae.otter.neotropical.api.domain.cancel.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.domain.booking.api.PrePayBookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePrePayOfOnlyCredit
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1.BookingGWControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import org.junit.jupiter.api.Test

internal class BookingCancelControllerTest : FlowTestSupport() {

    @Test
    fun cancelUsingCallOff() {
        // Given
        val userId = 57127431547L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)
        val cancelFlow = BookingCancelControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfOnlyCredit(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        cancelFlow.cancel(checkoutRequest.userId, bookingIds)

        // Then
        val recordDetail = bookingGWFlow.recordDetail(bookingIds.first())

        recordDetail.asClue {
            it.status.name shouldBe Booking.Status.CANCELED.text
            it.receipt.totalRefundAmount shouldNotBe null
            it.receipt.canceledDate shouldNotBe null
        }
    }
}
