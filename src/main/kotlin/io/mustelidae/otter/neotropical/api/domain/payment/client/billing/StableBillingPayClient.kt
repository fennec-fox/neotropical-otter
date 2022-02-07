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

    override fun repay(
        paymentId: Long,
        amountOfPay: Long,
        adjustmentId: Long?
    ): BillingClientResources.Reply.PaidResult {
        TODO("Not yet implemented")
    }

    override fun cancelEntire(
        paymentId: Long,
        cause: String
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelEntireWithPenalty(
        paymentId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelPartial(
        paymentId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelPartialWithPenalty(
        paymentId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun findByReceipt(productCode: ProductCode, paymentId: Long): PaidReceipt {
        TODO("Not yet implemented")
    }
}
