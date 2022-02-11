package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Topic
import io.mustelidae.otter.neotropical.api.config.FlowTestSupport
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1.BookingGWControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutControllerFlow
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.checkout.api.aFixtureByMultiProduct
import org.junit.jupiter.api.Test

internal class PrePayBookingControllerTest : FlowTestSupport() {

    @Test
    fun completeBookingByCreditCardAndPoint() {
        // Given
        val userId = 427325304L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfCreditWithPoint(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        val activeBookings = bookingGWFlow.activeBookings()

        prePayBookingFlow.complete(productCode, listOf(bookingIds.first()))

        val recordBookings = bookingGWFlow.recordBookings()

        // Then
        activeBookings.size shouldBe bookingIds.size
        activeBookings.size shouldBe 2
        recordBookings.size shouldBe 1
    }

    @Test
    fun completedBookingRecordByCreditCard() {
        // Given
        val userId = 138074183L
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
        val targetBookingId = bookingIds.first()

        prePayBookingFlow.complete(productCode, listOf(targetBookingId))

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.landingPath shouldNotBe null

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldNotBe null
            it.receipt.point shouldBe null
            it.receipt.voucher shouldBe null
            it.receipt.discountCoupon shouldBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }

    @Test
    fun completedBookingRecordByVoucher() {
        // Given
        val userId = 138074183L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfVoucher(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        val targetBookingId = bookingIds.first()

        prePayBookingFlow.complete(productCode, listOf(targetBookingId))

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.landingPath shouldNotBe null

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldBe null
            it.receipt.point shouldBe null
            it.receipt.voucher shouldNotBe null
            it.receipt.discountCoupon shouldBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }

    @Test
    fun completedBookingRecordByPointAndDiscount() {
        // Given
        val userId = 31742379324L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfPointDiscount(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        val targetBookingId = bookingIds.first()

        prePayBookingFlow.complete(productCode, listOf(targetBookingId))

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.landingPath shouldNotBe null

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldBe null
            it.receipt.point shouldNotBe null
            it.receipt.discountCoupon shouldNotBe null
            it.receipt.voucher shouldBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }

    @Test
    fun completedBookingRecordByCreditAndDiscount() {
        // Given
        val userId = 472347234L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val prePayBookingFlow = PrePayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePrePayOfCreditDiscount(orderId)
        val bookingIds = prePayBookingFlow.preBook(userId, bookingRequest)
        val targetBookingId = bookingIds.first()

        prePayBookingFlow.complete(productCode, listOf(targetBookingId))

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.landingPath shouldNotBe null

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldNotBe null
            it.receipt.point shouldBe null
            it.receipt.discountCoupon shouldNotBe null
            it.receipt.voucher shouldBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }
}
