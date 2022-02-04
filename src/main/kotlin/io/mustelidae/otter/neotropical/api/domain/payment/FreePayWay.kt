package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import java.time.LocalDateTime

class FreePayWay : PayWay {
    override val payment: Payment

    override fun pay(amountOfPay: Long, orderSheet: OrderSheet, adjustmentId: Long) {
        payment.pay(amountOfPay, orderSheet.id, null, adjustmentId)
    }

    override fun repay(amountOfRepay: Long, adjustmentId: Long?) {
        payment.repay(amountOfRepay, adjustmentId)
    }

    override fun cancel(cause: String) {
        if (payment.status != Payment.Status.PAY)
            throw DevelopMistakeException("Cancellation is possible only after payment has been completed.")

        payment.cancelEntire(LocalDateTime.now(), 0)
    }

    override fun cancelWithPenalty(cause: String, amountOfPenalty: Long) {
        payment.cancelEntire(LocalDateTime.now(), 0)
    }

    override fun cancelPartial(cause: String, partialCanceledAmount: Long) {
        payment.cancelPartial(LocalDateTime.now(), partialCanceledAmount, 0)
    }

    override fun cancelPartialWithPenalty(cause: String, partialCanceledAmount: Long, amountOfPenalty: Long) {
        payment.cancelPartial(LocalDateTime.now(), partialCanceledAmount, 0)
    }

    constructor(payment: Payment) {
        this.payment = payment
    }

    constructor(userId: Long, priceOfOrder: Long) {
        this.payment = Payment(userId, priceOfOrder, PayWay.Type.FREE)
    }
}
