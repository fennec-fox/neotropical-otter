package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.client.voucher.VoucherClient

class VoucherPayWay : PayWay {
    override val payment: Payment
    private val voucherClient: VoucherClient

    override fun pay(amountOfPay: Long, orderSheet: OrderSheet, adjustmentId: Long) {
        val voucher = orderSheet.estimateUsingPayMethod!!.voucher!!
        this.payment.pay(amountOfPay, orderSheet.id, null, adjustmentId)
        this.payment.usingVoucher(voucher)
        voucherClient.use(orderSheet.userId, orderSheet.productCode, orderSheet.topicId, voucher)
    }

    override fun repay(amountOfRepay: Long, adjustmentId: Long?) {
        throw DevelopMistakeException("Voucher payment does not support repayment")
    }

    override fun cancel(cause: String) {
        this.payment.cancelByChangeOnlyStatus()
        voucherClient.cancel(payment.userId, payment.voucherId!!)
    }

    override fun cancelWithPenalty(cause: String, amountOfPenalty: Long) {
        throw DevelopMistakeException("Voucher payment does not cancellation")
    }

    override fun cancelPartial(cause: String, partialCancelAmount: Long) {
        throw DevelopMistakeException("Voucher payment does not partial cancellation")
    }

    override fun cancelPartialWithPenalty(cause: String, partialCancelAmount: Long, amountOfPenalty: Long) {
        throw DevelopMistakeException("Voucher payment does not partial cancellation")
    }

    constructor(voucherClient: VoucherClient, payment: Payment) {
        this.payment = payment
        this.voucherClient = voucherClient
    }

    constructor(voucherClient: VoucherClient, userId: Long, priceOfOrder: Long) {
        this.payment = Payment(userId, priceOfOrder)
        this.voucherClient = voucherClient
    }
}
