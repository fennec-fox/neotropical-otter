package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.DefaultPayPayload
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.PayType
import java.time.LocalDate

class BillingPrePayWay : PayWay {

    override val payment: Payment
    private val billingPayClient: BillingPayClient

    override fun pay(
        amountOfPay: Long,
        orderSheet: OrderSheet,
        adjustmentId: Long
    ) {
        val paymentOrderId = orderSheet.id
        val accountSettlementDate = this.calculateLastSettlementDate(orderSheet)
        val usingPayMethod = orderSheet.estimateUsingPayMethod ?: throw DevelopMistakeException(ErrorCode.PD02)

        payment.pay(amountOfPay, paymentOrderId, usingPayMethod.creditCard?.payKey, adjustmentId)

        val payload = DefaultPayPayload(
            orderSheet.productCode,
            orderSheet.topicId,
            payment.userId,
            PayType.PAYOUT,
            adjustmentId,
            paymentOrderId.toString(),
            orderSheet.products.joinToString { it.title },
            accountSettlementDate,
            amountOfPay,
            usingPayMethod,
            orderSheet.preDefineField
        )

        val paidResult = billingPayClient.pay(orderSheet.userId, payload)

        payment.paid(
            paidResult.paymentId,
            paidResult.amountOfPaid,
            paidResult.paidMethods.map { it.method },
            paidResult.transactionDate
        )
    }

    private fun calculateLastSettlementDate(orderSheet: OrderSheet): LocalDate {
        val reserveDates = orderSheet.products.filter { it.reservationDate != null }
            .map { it.reservationDate!!.toLocalDate() }
            .toMutableList()

        orderSheet.settlementDate?.let {
            reserveDates.add(it)
        }

        return reserveDates.maxOf { it }
    }

    override fun repay(amountOfRepay: Long, adjustmentId: Long?) {
        if (payment.isPaid().not())
            throw DevelopMistakeException("Repayment is not possible.")

        val paidResult = billingPayClient.repay(payment.paymentId!!, amountOfRepay, adjustmentId)
        payment.paid(
            paidResult.paymentId,
            paidResult.amountOfPaid,
            paidResult.paidMethods.map { it.method },
            paidResult.transactionDate
        )
        payment.adjustmentId = adjustmentId
    }

    override fun cancel(cause: String) {
        val result = billingPayClient.cancelEntire(payment.paymentId!!, cause)
        payment.cancelEntire(result.transactionDate, 0)
    }

    override fun cancelWithPenalty(cause: String, amountOfPenalty: Long) {
        val result = billingPayClient.cancelEntireWithPenalty(payment.paymentId!!, cause, amountOfPenalty)
        payment.cancelEntire(result.transactionDate, result.penaltyAmount!!)
    }

    override fun cancelPartial(cause: String, partialCancelAmount: Long) {
        val result = billingPayClient.cancelPartial(payment.paymentId!!, cause, partialCancelAmount)
        payment.cancelPartial(result.transactionDate, partialCancelAmount, 0)
    }

    override fun cancelPartialWithPenalty(cause: String, partialCancelAmount: Long, amountOfPenalty: Long) {
        val result = billingPayClient.cancelPartialWithPenalty(payment.paymentId!!, cause, partialCancelAmount, amountOfPenalty)
        payment.cancelPartial(result.transactionDate, partialCancelAmount, amountOfPenalty)
    }

    constructor(
        billingPayClient: BillingPayClient,
        userId: Long,
        priceOfOrder: Long,
    ) {
        this.payment = Payment(userId, priceOfOrder, PayWay.Type.PRE_PAY)
        this.billingPayClient = billingPayClient
    }

    constructor(billingPayClient: BillingPayClient, payment: Payment) {
        this.payment = payment
        this.billingPayClient = billingPayClient
    }
}
