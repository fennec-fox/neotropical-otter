package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt
import org.springframework.web.reactive.function.client.WebClient

class StableBillingPayClient(
    private val webClient: WebClient
) : BillingPayClient {
    override fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult {
        TODO("Not yet implemented")
    }

    override fun cancelEntire(userId: Long, billPayId: Long, cause: String): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelEntireWithPenalty(
        userId: Long,
        billPayId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelPartial(
        userId: Long,
        billPayId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelPartialWithPenalty(
        userId: Long,
        billPayId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun findByReceipt(productCode: ProductCode, userId: Long, billPayId: Long): PaidReceipt {
        TODO("Not yet implemented")
    }
}
