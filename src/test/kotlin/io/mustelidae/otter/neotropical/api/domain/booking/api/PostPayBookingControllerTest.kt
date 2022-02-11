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

internal class PostPayBookingControllerTest : FlowTestSupport() {

    @Test
    fun completedBookingByOnlyCreditCard() {
        // Given
        val userId = 237423742L
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
        postPayBookingFlow.complete(productCode, bookingIds, null)
        val targetBookingId = bookingIds.first()

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldNotBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.point shouldBe null
            it.receipt.discountCoupon shouldBe null
            it.receipt.voucher shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }

    @Test
    fun completedBookingByOnlyCreditCardWithDiscount() {
        // Given
        val userId = 237423742L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfCreditWithDiscount(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        postPayBookingFlow.complete(productCode, bookingIds, null)
        val targetBookingId = bookingIds.first()

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldNotBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.point shouldBe null
            it.receipt.discountCoupon shouldNotBe null
            it.receipt.voucher shouldBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }

    @Test
    fun completedBookingByOnlyVoucher() {
        // Given
        val userId = 237423742L
        val productCode = ProductCode.MOCK_UP
        val topicId = Topic.topicMap[productCode]!!.first()
        val checkoutFlow = CheckoutControllerFlow(productCode, mockMvc)
        val postPayBookingFlow = PostPayBookingControllerFlow(mockMvc)
        val bookingGWFlow = BookingGWControllerFlow(mockMvc, userId)

        // When
        val checkoutRequest = CheckoutResources.Request.Checkout.aFixtureByMultiProduct(userId)
        val orderId = checkoutFlow.checkout(topicId, checkoutRequest)
        val bookingRequest = BookingResources.Request.aFixturePostPayOfVoucher(orderId)
        val bookingIds = postPayBookingFlow.postBook(userId, bookingRequest)
        postPayBookingFlow.complete(productCode, bookingIds, null)
        val targetBookingId = bookingIds.first()

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.point shouldBe null
            it.receipt.discountCoupon shouldBe null
            it.receipt.voucher shouldNotBe null
            it.receipt.canceledDate shouldBe null
            it.receipt.paidDate shouldNotBe null
        }
    }

    @Test
    fun bookingByCardHoweverChangeAmount() {
        // Given
        val userId = 237423742L
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
        val completeRequest = BookingResources.Request.PostPayCompleteBook(
            100000
        )

        postPayBookingFlow.complete(productCode, bookingIds, completeRequest)
        val targetBookingId = bookingIds.first()

        // Then
        val detail = bookingGWFlow.recordDetail(targetBookingId)

        detail.asClue {
            it.productCode shouldBe productCode
            it.landingType shouldBe LandingWay.WEB_VIEW
            it.title shouldBe checkoutRequest.products.first().title
            it.receipt.creditCard shouldNotBe null
            it.receipt.totalPaidAmount shouldBe completeRequest.changeAmount
            it.receipt.paidDate shouldNotBe null
        }
    }
}
