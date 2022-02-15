package io.mustelidae.otter.neotropical.api.domain.cancel.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.Item
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingMaintenanceControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.domain.booking.api.PostPayBookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.PrePayBookingControllerFlow
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePostPayOfCreditWithDiscount
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePrePayOfCreditWithDiscount
import io.mustelidae.otter.neotropical.api.domain.booking.api.aFixturePrePayOfOnlyCredit
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1.BookingGWControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import org.junit.jupiter.api.Test

internal class BookingCancelControllerTest : FlowTestSupport() {

    @Test
    fun cancelOrder() {
        // Given
        val userId = 17943219347L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)
        val cancelFlow = BookingCancelControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfCreditWithDiscount(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        cancelFlow.cancel(userId, orderId)

        // Then
        val records = bookingGWFlow.recordBookings()

        bookingIds.forEach { id ->
            val recordDetail = bookingGWFlow.recordDetail(id)

            recordDetail.asClue {
                it.status.name shouldBe Booking.Status.CANCELED.text
                it.receipt.totalRefundAmount shouldNotBe null
                it.receipt.canceledDate shouldNotBe null
            }
        }

        records.forEach {
            it.status.name shouldBe Booking.Status.CANCELED.text
        }
    }

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

    @Test
    fun cancelBook() {
        // Given
        val userId = 2374283475L
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
        val targetBookingId = bookingIds.first()
        cancelFlow.cancel(checkoutRequest.userId, listOf(targetBookingId))

        // Then
        val records = bookingGWFlow.recordBookings()
        val recordDetail = bookingGWFlow.recordDetail(targetBookingId)

        recordDetail.asClue {
            it.status.name shouldBe Booking.Status.CANCELED.text
            it.receipt.totalRefundAmount shouldNotBe null
            it.receipt.canceledDate shouldNotBe null
        }

        records.size shouldBe 1
    }

    @Test
    fun cancelItem() {
        // Given
        val userId = 234725457239L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val bookingFlow = PostPayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)
        val cancelFlow = BookingCancelControllerFlow(mockMvc)
        val maintenanceFlow = BookingMaintenanceControllerFlow(mockMvc)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfCreditWithDiscount(orderId)
        val bookingIds = bookingFlow.postBook(userId, bookingRequest)
        val targetBookingId = bookingIds.first()
        val reply = maintenanceFlow.findOne(targetBookingId)
        bookingFlow.complete(productCode, bookingIds, null)

        val itemId = reply.items.first().id
        cancelFlow.cancel(userId, targetBookingId, listOf(itemId))

        // Then
        val records = bookingGWFlow.recordBookings()
        val recordDetail = bookingGWFlow.recordDetail(targetBookingId)

        recordDetail.asClue {
            it.receipt.totalRefundAmount shouldNotBe null
            it.receipt.canceledDate shouldNotBe null
        }
        recordDetail.items.find { it.status == Item.Status.CANCELED } shouldNotBe null
        records.size shouldBe 2
    }
}
