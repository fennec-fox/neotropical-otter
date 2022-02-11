package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.DefaultPayPayload
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.PayType
import java.time.LocalDate

class BillingPostPayWay : PayWay {
    override val payment: Payment
    private val billingPayClient: BillingPayClient

    override fun pay(amountOfPay: Long, orderSheet: OrderSheet, adjustmentId: Long) {
        val bookOrderId = orderSheet.id
        val usingPayMethod = orderSheet.estimateUsingPayMethod ?: throw DevelopMistakeException(ErrorCode.PD02)

        payment.pay(amountOfPay, bookOrderId, usingPayMethod.creditCard?.payKey, adjustmentId)

        val payload = DefaultPayPayload(
            orderSheet.productCode,
            orderSheet.topicId,
            payment.userId,
            PayType.PAYOUT,
            adjustmentId,
            bookOrderId.toString(),
            orderSheet.products.joinToString { it.title },
            LocalDate.now(),
            amountOfPay,
            usingPayMethod,
            orderSheet.preDefineField
        )

        val paidResult = billingPayClient.pay(orderSheet.userId, payload)

        payment.paid(
            paidResult.billPayId,
            paidResult.amountOfPaid,
            paidResult.paidMethods.map { it.method },
            paidResult.transactionDate
        )
    }

    override fun repay(amountOfRepay: Long, adjustmentId: Long?) {
        BillingPrePayWay(payment, billingPayClient).repay(amountOfRepay, adjustmentId)
    }

    override fun cancel(cause: String) {
        if (this.payment.isPaid())
            BillingPrePayWay(payment, billingPayClient).cancel(cause)
        else {
            this.payment.cancelByChangeOnlyStatus()
        }
    }

    override fun cancelWithPenalty(cause: String, amountOfPenalty: Long) {
        if (this.payment.isPaid())
            BillingPrePayWay(payment, billingPayClient).cancelWithPenalty(cause, amountOfPenalty)
        else {
            this.payment.cancelByChangeOnlyStatus()
        }
    }

    override fun cancelPartial(cause: String, partialCancelAmount: Long) {
        throw DevelopMistakeException("Postpaid payment does not support partial cancellation.")
    }

    override fun cancelPartialWithPenalty(cause: String, partialCancelAmount: Long, amountOfPenalty: Long) {
        throw DevelopMistakeException("Postpaid payment does not support partial cancellation.")
    }

    constructor(payment: Payment, billingPayClient: BillingPayClient) {
        this.payment = payment
        this.billingPayClient = billingPayClient
    }

    constructor(
        userId: Long,
        priceOfOrder: Long,
        billingPayClient: BillingPayClient
    ) {
        this.payment = Payment(userId, priceOfOrder, PayWay.Type.POST_PAY)
        this.billingPayClient = billingPayClient
    }
}
