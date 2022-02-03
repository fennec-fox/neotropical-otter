package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

interface BillingPayClient {

    fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult

    fun repay(paymentId: Long, amountOfPay: Long, adjustmentId: Long?): BillingClientResources.Reply.PaidResult

    fun cancelEntire(
        paymentId: Long,
        cause: String
    ): BillingClientResources.Reply.CancelResult

    fun cancelEntireWithPenalty(
        paymentId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartial(
        paymentId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartialWithPenalty(
        paymentId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult
}
