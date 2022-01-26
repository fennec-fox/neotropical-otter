package io.mustelidae.otter.neotropical.api.domain.payment.client

interface BillingPayClient {

    fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult

    fun cancelEntire(
        paymentId: Long,
        paymentOrderId: String,
        cause: String
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartial(
        paymentId: Long,
        paymentOrderId: String,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult
}
