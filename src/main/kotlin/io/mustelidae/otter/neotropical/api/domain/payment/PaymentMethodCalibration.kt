package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient
import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.Point
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient
import org.springframework.stereotype.Service

@Service
class PaymentMethodCalibration(
    private val billingPaymentMethodClient: BillingPaymentMethodClient,
    private val voucherClient: VoucherClient
) {

    fun calibrate(userId: Long, orderSheet: OrderSheet, changePaymentMethod: UsingPayMethod?): UsingPayMethod {
        val orderedPayMethod = orderSheet.estimateUsingPayMethod!!

        var creditCard: CreditCard? = null
        var point: Point? = null
        var discountCoupon: DiscountCoupon? = null
        var voucher: Voucher? = null

        if (changePaymentMethod != null) {
            changePaymentMethod.fillUpDetailAll(userId, billingPaymentMethodClient, voucherClient)

            if (changePaymentMethod.creditCard != null && changePaymentMethod.creditCard.isValid)
                creditCard = changePaymentMethod.creditCard

            if (changePaymentMethod.discountCoupon != null && changePaymentMethod.discountCoupon.isValid)
                discountCoupon = changePaymentMethod.discountCoupon

            if (changePaymentMethod.point != null && changePaymentMethod.point.isValid)
                point = changePaymentMethod.point

            if (changePaymentMethod.voucher != null && changePaymentMethod.voucher.isValid)
                voucher = changePaymentMethod.voucher
        }

        return UsingPayMethod(
            creditCard ?: getValidCard(orderedPayMethod.creditCard!!, userId),
            point ?: orderedPayMethod.point,
            discountCoupon ?: orderedPayMethod.discountCoupon,
            voucher ?: orderedPayMethod.voucher
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
