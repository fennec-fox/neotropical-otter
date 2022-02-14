package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt

interface BillingPayClient {

    fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult

    fun cancelEntire(
        userId: Long,
        billPayId: Long,
        cause: String
    ): BillingClientResources.Reply.CancelResult

    fun cancelEntireWithPenalty(
        userId: Long,
        billPayId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartial(
        userId: Long,
        billPayId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun cancelPartialWithPenalty(
        userId: Long,
        billPayId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult

    fun findByReceipt(
        productCode: ProductCode,
        userId: Long,
        billPayId: Long
    ): PaidReceipt
}
