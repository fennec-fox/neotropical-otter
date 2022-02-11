package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient
import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient
import org.springframework.stereotype.Service

@Service
class PaymentMethodCalibration(
    private val billingPaymentMethodClient: BillingPaymentMethodClient,
    private val voucherClient: VoucherClient
) {

    fun calibrate(userId: Long, orderSheet: OrderSheet, changePaymentMethod: UsingPayMethod?): UsingPayMethod {
        val orderedPayMethod = orderSheet.estimateUsingPayMethod!!

        var sourceCreditCard = orderedPayMethod.creditCard
        var sourcePoint = orderedPayMethod.point
        var sourceDiscountCoupon = orderedPayMethod.discountCoupon
        var sourceVoucher = orderedPayMethod.voucher

        changePaymentMethod?.let {
            it.fillUpDetailAll(userId, billingPaymentMethodClient, voucherClient)

            if (it.creditCard != null && it.creditCard.isValid)
                sourceCreditCard = it.creditCard

            if (it.discountCoupon != null && it.discountCoupon.isValid)
                sourceDiscountCoupon = it.discountCoupon

            if (it.point != null && it.point.isValid)
                sourcePoint = it.point

            if (it.voucher != null && it.voucher.isValid)
                sourceVoucher = it.voucher
        }

        // If you have a voucher, use it only.
        if (sourceVoucher != null) {
            sourceVoucher!!.fillUpDetail(userId, voucherClient)
            if (sourceVoucher!!.isValid)
                return UsingPayMethod(null, null, null, sourceVoucher)
        }

        var normalcyDiscountCoupon: DiscountCoupon? = null

        if (sourceDiscountCoupon != null) {
            sourceDiscountCoupon!!.fillUpDetail(userId, billingPaymentMethodClient)
            if (sourceDiscountCoupon!!.isValid)
                normalcyDiscountCoupon = sourceDiscountCoupon
        }

        return UsingPayMethod(
            getValidCard(sourceCreditCard!!, userId),
            sourcePoint,
            normalcyDiscountCoupon,
            null
        )
    }

    private fun getValidCard(
        creditCard: CreditCard,
        userId: Long
    ): CreditCard {
        creditCard.fillUpDetail(userId, billingPaymentMethodClient)
        return if (creditCard.isValid) {
            creditCard
        } else {
            val detail = billingPaymentMethodClient.findAvailableCard(userId)
            val availableCard = CreditCard(detail.payKey)
            availableCard.fillUpDetail(userId, billingPaymentMethodClient)
            availableCard
        }
    }
}
