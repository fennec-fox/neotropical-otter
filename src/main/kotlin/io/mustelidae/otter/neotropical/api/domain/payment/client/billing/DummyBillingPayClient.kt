package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaidCreditCard
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaidDiscountCoupon
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaidPoint
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaymentMethod
import java.time.LocalDateTime
import kotlin.random.Random

class DummyBillingPayClient : BillingPayClient {

    private val localStore = mutableListOf<DummyLocalRaw>()

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

        localStore.add(
            DummyLocalRaw(
                userId, payPayload, methods, false, Random.nextLong()
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

        val raw = this.localStore.find { it.paymentId == paymentId }!!

        raw.apply {
            val repayPayload = this.payPayload.run {
                DefaultPayPayload(
                    productCode,
                    topicId,
                    userId,
                    type,
                    this.adjustmentId,
                    paymentOrderId,
                    itemName,
                    accountSettlementDate,
                    amountOfPay,
                    usingPayMethod,
                    preDefineValue
                )
            }
            this.payPayload = repayPayload
        }

        raw.paymentId = Random.nextLong()

        return BillingClientResources.Reply.PaidResult(
            raw.paymentId!!,
            amountOfPay,
            raw.methods, LocalDateTime.now()
        )
    }

    override fun cancelEntire(
        paymentId: Long,
        cause: String
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.paymentId == paymentId }!!
        raw.isCancel = true

        return BillingClientResources.Reply.CancelResult(
            paymentId,
            raw.methods,
            LocalDateTime.now()
        )
    }

    override fun cancelEntireWithPenalty(
        paymentId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.paymentId == paymentId }!!
        raw.isCancel = true
        raw.refundAmount = raw.payPayload.amountOfPay - penaltyAmount
        raw.penaltyAmount = penaltyAmount

        return BillingClientResources.Reply.CancelResult(
            paymentId,
            raw.methods,
            LocalDateTime.now(),
            penaltyAmount
        )
    }

    override fun cancelPartial(
        paymentId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.paymentId == paymentId }!!
        raw.isCancel = true
        raw.refundAmount = raw.payPayload.amountOfPay - cancelAmount

        return BillingClientResources.Reply.CancelResult(
            paymentId,
            raw.methods,
            LocalDateTime.now()
        )
    }

    override fun cancelPartialWithPenalty(
        paymentId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.paymentId == paymentId }!!
        raw.isCancel = true
        raw.refundAmount = raw.payPayload.amountOfPay - (cancelAmount + penaltyAmount)
        raw.penaltyAmount = penaltyAmount

        return BillingClientResources.Reply.CancelResult(
            paymentId,
            raw.methods,
            LocalDateTime.now(),
            penaltyAmount
        )
    }

    override fun findByReceipt(productCode: ProductCode, paymentId: Long): PaidReceipt {
        val raw = this.localStore.find { it.paymentId == paymentId }!!
        val credit = raw.methods.find { it.method == PaymentMethod.CARD }?.let {
            val client = DummyBillingPaymentMethodClient()
            val card = client.findCard(raw.userId, raw.payPayload.usingPayMethod.creditCard!!.payKey)
            PaidCreditCard(
                card.payKey,
                card.name,
                card.number,
                paidAmount = it.amount
            )
        }

        val point = raw.methods.find { it.method == PaymentMethod.POINT }?.let {
            PaidPoint(it.amount)
        }

        val discountCoupon = raw.methods.find { it.method == PaymentMethod.DISCOUNT_COUPON }?.let {
            PaidDiscountCoupon(
                raw.payPayload.usingPayMethod.discountCoupon!!.id,
                raw.payPayload.usingPayMethod.discountCoupon?.groupId,
                it.amount
            )
        }

        return PaidReceipt(
            raw.payPayload.amountOfPay,
            LocalDateTime.now(),
            raw.isCancel,
            credit,
            point,
            discountCoupon,
            raw.refundAmount,
            LocalDateTime.now()
        )
    }

    data class DummyLocalRaw(
        var userId: Long,
        var payPayload: PayPayload,
        var methods: List<BillingClientResources.Reply.MethodAmountPair>,
        var isCancel: Boolean = false,
        var paymentId: Long? = null,
        var refundAmount: Long? = null,
        var penaltyAmount: Long? = null
    )
}
