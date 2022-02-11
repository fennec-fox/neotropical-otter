package io.mustelidae.otter.neotropical.api.domain.payment.method

import com.fasterxml.jackson.annotation.JsonIgnore
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient
import java.time.LocalDateTime

open class DiscountCoupon(
    open val id: Long,
    open val groupId: Long? = null,
) : PayMethod {

    open var name: String? = null
    open var discountAmount: Long? = null
    open var condition: String? = null
    open var startDate: LocalDateTime? = null
    open var endDate: LocalDateTime? = null
    @Transient
    override var isValid: Boolean = true
    @Transient
    override var paymentMethod: PaymentMethod = PaymentMethod.DISCOUNT_COUPON
        protected set

    @JsonIgnore
    fun fillUpDetail(userId: Long, billingPaymentMethodClient: BillingPaymentMethodClient) {
        val detail = billingPaymentMethodClient.findDiscountCoupon(userId, id, groupId)
        this.name = detail.name
        this.discountAmount = detail.discountAmount
        this.condition = detail.condition
        this.startDate = detail.startDate
        this.endDate = detail.endDate
        this.isValid = detail.status
        val now = LocalDateTime.now()

        if (startDate != null && now < startDate)
            isValid = false

        if (endDate != null && now > endDate)
            isValid = false
    }
}
