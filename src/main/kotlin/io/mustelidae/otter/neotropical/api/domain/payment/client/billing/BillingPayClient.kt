package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt

interface BillingPayClient {

    fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult

    fun repay(billPayId: Long, amountOfPay: Long, adjustmentId: Long?): BillingClientResources.Reply.PaidResult

    fun cancelEntire(
        billPayId: Long,
        cause: String
    ): BillingClientResources.Reply.CancelResult

    fun cancelEntireWithPenalty(
        billPayId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartial(
        billPayId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartialWithPenalty(
        billPayId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun findByReceipt(productCode: ProductCode, billPayId: Long): PaidReceipt
}
