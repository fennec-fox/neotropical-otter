package io.mustelidae.otter.neotropical.api.domain.vertical.api

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.Item
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingMaintenanceControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.domain.booking.api.PostPayBookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePostPayOfCredit
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePostPayOfVoucher
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByOnce
import org.junit.jupiter.api.Test

internal class VerticalBookingControllerTest : FlowTestSupport() {

    @Test
    fun cancel() {
        // Given
        val userId = 342734274L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val cancelFlow = VerticalBookingControllerFlow(mockMvc)
        val maintenanceFlow = BookingMaintenanceControllerFlow(mockMvc)
        val booingFlow = BookingControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByOnce(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfCredit(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        booingFlow.confirm(productCode, bookingIds)
        postPayBookingFlow.complete(productCode, bookingIds, null)

        cancelFlow.cancelAll(userId, orderId, productCode)

        // Then
        val booking = maintenanceFlow.findOne(bookingIds.first())

        booking.status shouldBe Booking.Status.CANCELED
        val groupOfStatus = booking.items.groupBy { it.status }
        groupOfStatus[Item.Status.ORDERED] shouldBe null
        groupOfStatus[Item.Status.COMPLETED] shouldBe null
    }

    @Test
    fun testCancel() {
        // Given
        val userId = 7879872347L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val cancelFlow = VerticalBookingControllerFlow(mockMvc)
        val maintenanceFlow = BookingMaintenanceControllerFlow(mockMvc)
        val booingFlow = BookingControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfVoucher(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        booingFlow.confirm(productCode, bookingIds)
        postPayBookingFlow.complete(productCode, bookingIds, null)

        cancelFlow.cancelAll(userId, orderId, productCode)

        // Then
        val booking = maintenanceFlow.findOne(bookingIds.first())

        booking.status shouldBe Booking.Status.CANCELED
        val groupOfStatus = booking.items.groupBy { it.status }
        groupOfStatus[Item.Status.ORDERED] shouldBe null
        groupOfStatus[Item.Status.CANCELED] shouldNotBe null
    }

    @Test
    fun recentBooking() {
        // Given
        val userId = 274325723L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val verticalFlow = VerticalBookingControllerFlow(mockMvc)
        val booingFlow = BookingControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfVoucher(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        booingFlow.confirm(productCode, bookingIds)
        postPayBookingFlow.complete(productCode, bookingIds, null)

        // Then
        val recentBookings = verticalFlow.recentBooking(productCode, userId)

        recentBookings.size shouldBeGreaterThan 0
    }
}
