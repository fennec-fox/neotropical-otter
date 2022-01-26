package io.mustelidae.otter.neotropical.api.domain.payment.client

import io.mustelidae.otter.neotropical.api.domain.payment.PaymentMethod
import java.time.LocalDateTime
import kotlin.random.Random

class DummyBillingPayClient : BillingPayClient {
    override fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult {

        val methods = listOf(
            BillingClientResources.Reply.MethodAmountPair(
                PaymentMethod.CARD,
                payPayload.amountOfPay - payPayload.amountOfPoint
            ),
            BillingClientResources.Reply.MethodAmountPair(
                PaymentMethod.POINT,
                payPayload.amountOfPoint
            )
        )

        return BillingClientResources.Reply.PaidResult(
            Random.nextLong(),
            payPayload.amountOfPay,
            methods, LocalDateTime.now()
        )
    }

    override fun cancelEntire(
        paymentId: Long,
        paymentOrderId: String,
        cause: String
    ): BillingClientResources.Reply.CancelResult {
        return BillingClientResources.Reply.CancelResult(
            paymentId,
            listOf(),
            LocalDateTime.now()
        )
    }

    override fun cancelPartial(
        paymentId: Long,
        paymentOrderId: String,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        return BillingClientResources.Reply.CancelResult(
            paymentId,
            listOf(),
            LocalDateTime.now()
        )
    }
}
