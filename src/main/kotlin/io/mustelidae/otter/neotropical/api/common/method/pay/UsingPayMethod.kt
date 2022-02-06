package io.mustelidae.otter.neotropical.api.common.method.pay

import com.fasterxml.jackson.annotation.JsonIgnore

data class UsingPayMethod(
    val creditCard: CreditCard?,
    val point: Point?,
    val discountCoupon: DiscountCoupon?,
    val voucher: Voucher?
) {
    @JsonIgnore
    fun hasCard(): Boolean = (creditCard != null)

    @JsonIgnore
    private fun isValid(): Boolean {
        var count: Int = 0
        if (creditCard != null)
            count++
        if (point != null)
            count++
        if (discountCoupon != null)
            count++
        if (voucher != null)
            count++

        return (count > 0)
    }

    @JsonIgnore
    fun validOrThrow() {
        if (this.isValid().not())
            throw IllegalArgumentException("There is no payment method available.")
    }
}
