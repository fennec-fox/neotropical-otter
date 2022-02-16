package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.kotest.assertions.asClue
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import org.junit.jupiter.api.Test

internal class BookingMaintenanceControllerTest : FlowTestSupport() {

    @Test
    fun findOne() {
        // Given
        val userId = 23472347L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val maintenanceFlow = BookingMaintenanceControllerFlow(mockMvc)
        val bookFlow = BookingControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfCredit(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        bookFlow.confirm(productCode, bookingIds)
        postPayBookingFlow.complete(productCode, bookingIds, null)
        val targetBookingId = bookingIds.first()
        // Then
        val reply = maintenanceFlow.findOne(targetBookingId)

        reply.asClue {
            it.id shouldBe targetBookingId
            it.items.size shouldBeGreaterThan 0
        }
    }

    @Test
    fun search() {
        // Given
        val userId = 23472347L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val maintenanceFlow = BookingMaintenanceControllerFlow(mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookFlow = BookingControllerFlow(mockMvc)

        // When

        // Post Book
        val requestOfPostBook = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderIdOfPostBook = checkoutFlow.checkout(topicId, requestOfPostBook)
        val requestBookOfPostBook = BookingResources.Request.aFixturePostPayOfCredit(orderIdOfPostBook)
        val bookingIdsOfPostBook = postPayBookingFlow.postBook(userId, requestBookOfPostBook)
        bookFlow.confirm(productCode, bookingIdsOfPostBook)
        postPayBookingFlow.complete(productCode, bookingIdsOfPostBook, null)

        // Pre Book
        val requestCheckoutOfPreBook = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderIdOfPreBook = checkoutFlow.checkout(topicId, requestCheckoutOfPreBook)
        val requestBookOfPreBook = BookingResources.Request.aFixturePrePayOfCreditWithPoint(orderIdOfPreBook)
        val bookingIdsOfPreBook = prePayBookingFlow.preBook(userId, requestBookOfPreBook)
        bookFlow.confirm(productCode, bookingIdsOfPreBook)
        prePayBookingFlow.complete(productCode, listOf(bookingIdsOfPreBook.first()))

        // Then
        val searchedBookings = maintenanceFlow.search(ProductCode.MOCK_UP, userId = userId)

        searchedBookings.size shouldBe 4
        val groupOfBooking = searchedBookings.groupBy { it.status }
        groupOfBooking[Booking.Status.COMPLETED]!!.size shouldBe 3
        groupOfBooking[Booking.Status.BOOKED]!!.size shouldBe 1
    }
}
