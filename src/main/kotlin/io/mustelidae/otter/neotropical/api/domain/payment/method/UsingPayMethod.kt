package io.mustelidae.otter.neotropical.api.domain.payment.method

import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient
import io.mustelidae.otter.neotropical.api.domain.payment.method.CreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.DiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.Point
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient

data class UsingPayMethod(
    val creditCard: CreditCard?,
    val point: Point?,
    val discountCoupon: DiscountCoupon?,
    val voucher: Voucher?
) {

    fun hasCard(): Boolean = (creditCard != null)

    private fun isValid(): Boolean {
        if (creditCard != null && creditCard.isValid.not())
            return false

        if (point != null && point.isValid.not())
            return false

        if (discountCoupon != null && discountCoupon.isValid.not())
            return false
        if (voucher != null && voucher.isValid.not())
            return false

        if (creditCard == null &&
            point == null &&
            discountCoupon == null &&
            voucher == null
        )
            return false

        return true
    }

    fun fillUpDetailAll(userId: Long, billingPaymentMethodClient: BillingPaymentMethodClient, voucherClient: VoucherClient) {
        this.creditCard?.fillUpDetail(userId, billingPaymentMethodClient)
        this.discountCoupon?.fillUpDetail(userId, billingPaymentMethodClient)
        this.voucher?.fillUpDetail(userId, voucherClient)
    }

    fun validOrThrow() {
        if (this.isValid().not())
            throw IllegalArgumentException("There is no payment method available.")
    }
}
