package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.method.pay.PaymentMethod
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt
import java.time.LocalDateTime
import kotlin.random.Random

class DummyBillingPayClient : BillingPayClient {
    override fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult {

        val methods = listOf(
            BillingClientResources.Reply.MethodAmountPair(
                PaymentMethod.CARD,
                payPayload.amountOfPay - (payPayload.usingPayMethod.point?.amount ?: 0)
            ),
            BillingClientResources.Reply.MethodAmountPair(
                PaymentMethod.POINT,
                (payPayload.usingPayMethod.point?.amount ?: 0)
            )
        )

        return BillingClientResources.Reply.PaidResult(
            Random.nextLong(),
            payPayload.amountOfPay,
            methods, LocalDateTime.now()
        )
    }

    override fun repay(
        paymentId: Long,
        amountOfPay: Long,
        adjustmentId: Long?
    ): BillingClientResources.Reply.PaidResult {
        return BillingClientResources.Reply.PaidResult(
            Random.nextLong(),
            amountOfPay,
            listOf(BillingClientResources.Reply.MethodAmountPair(PaymentMethod.CARD, amountOfPay)), LocalDateTime.now()
        )
    }

    override fun cancelEntire(
        paymentId: Long,
        cause: String
    ): BillingClientResources.Reply.CancelResult {
        return BillingClientResources.Reply.CancelResult(
            paymentId,
            listOf(),
            LocalDateTime.now()
        )
    }

    override fun cancelEntireWithPenalty(
        paymentId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        return BillingClientResources.Reply.CancelResult(
            paymentId,
            listOf(
                BillingClientResources.Reply.MethodAmountPair(PaymentMethod.CARD, 1000)
            ),
            LocalDateTime.now(),
            penaltyAmount
        )
    }

    override fun cancelPartial(
        paymentId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        return BillingClientResources.Reply.CancelResult(
            paymentId,
            listOf(),
            LocalDateTime.now()
        )
    }

    override fun cancelPartialWithPenalty(
        paymentId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {
        return BillingClientResources.Reply.CancelResult(
            paymentId,
            listOf(
                BillingClientResources.Reply.MethodAmountPair(PaymentMethod.CARD, 1000)
            ),
            LocalDateTime.now(),
            penaltyAmount
        )
    }

    override fun findByReceipt(productCode: ProductCode, paymentId: Long): PaidReceipt {
        TODO("Not yet implemented")
    }
}
