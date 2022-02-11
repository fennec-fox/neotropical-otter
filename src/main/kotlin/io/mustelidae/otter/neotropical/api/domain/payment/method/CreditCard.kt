package io.mustelidae.otter.neotropical.api.domain.payment.method

import com.fasterxml.jackson.annotation.JsonIgnore
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient

open class CreditCard(
    open val payKey: String
) : PayMethod {
    open var cardName: String? = null
    open var cardNumber: String? = null
    open var corpName: String? = null
    @Transient
    override var paymentMethod: PaymentMethod = PaymentMethod.CARD
        protected set
    @Transient
    override var isValid = true

    @JsonIgnore
    fun fillUpDetail(userId: Long, billingPaymentMethodClient: BillingPaymentMethodClient) {
        val credit = billingPaymentMethodClient.findCard(userId, payKey)
        cardName = credit.name
        cardNumber = credit.number
        corpName = credit.corp
        isValid = credit.status
    }
}
