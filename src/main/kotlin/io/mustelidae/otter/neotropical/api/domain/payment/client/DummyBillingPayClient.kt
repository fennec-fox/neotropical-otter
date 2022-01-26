package io.mustelidae.otter.neotropical.api.domain.payment.client

class DummyBillingPayClient : BillingPayClient {
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

    override fun cancelEntireWithPenalty(
        paymentId: Long,
        paymentOrderId: String,
        cause: String,
        penaltyAmount: Long
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

    override fun cancelPartialWithPenalty(
        paymentId: Long,
        paymentOrderId: String,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        TODO("Not yet implemented")
    }
}
