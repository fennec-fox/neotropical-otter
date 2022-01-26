package io.mustelidae.otter.neotropical.api.domain.payment.client

import io.mustelidae.otter.neotropical.api.common.Reply
import org.springframework.web.reactive.function.client.WebClient

class StableBillingPayClient(
    private val webClient: WebClient
) : BillingPayClient {
    override fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult {
        TODO("Not yet implemented")
    }

    override fun cancelEntire(
        paymentId: Long,
        paymentOrderId: String,
        cause: String
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }

    override fun cancelPartial(
        paymentId: Long,
        paymentOrderId: String,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }
}
