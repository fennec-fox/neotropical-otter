package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.method.PaidCreditCard
import io.mustelidae.otter.neotropical.api.common.method.PaidDiscountCoupon
import io.mustelidae.otter.neotropical.api.common.method.PaidPoint
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaymentMethod
import java.time.LocalDateTime
import kotlin.random.Random

class DummyBillingPayClient : BillingPayClient {

    private val localStore = mutableListOf<DummyLocalRaw>()

    override fun pay(userId: Long, payPayload: PayPayload): BillingClientResources.Reply.PaidResult {

        val methods = mutableListOf<BillingClientResources.Reply.MethodAmountPair>()
        var remainAmount = payPayload.amountOfPay

        payPayload.usingPayMethod.discountCoupon?.let {
            remainAmount -= it.discountAmount!!

            methods.add(
                BillingClientResources.Reply.MethodAmountPair(
                    PaymentMethod.DISCOUNT_COUPON,
                    it.discountAmount!!
                )
            )
        }

        payPayload.usingPayMethod.point?.let {
            remainAmount -= it.amount
            methods.add(
                BillingClientResources.Reply.MethodAmountPair(
                    PaymentMethod.POINT,
                    (it.amount)
                )
            )
        }

        if (remainAmount > 0) {
            methods.add(
                BillingClientResources.Reply.MethodAmountPair(
                    PaymentMethod.CARD,
                    remainAmount
                )
            )
        }

        val billPayId = Random.nextLong()
        localStore.add(
            DummyLocalRaw(
                userId, payPayload, methods, false, billPayId
            )
        )

        return BillingClientResources.Reply.PaidResult(
            billPayId,
            payPayload.amountOfPay,
            methods, LocalDateTime.now()
        )
    }

    override fun repay(
        userId: Long,
        billPayId: Long,
        amountOfPay: Long,
        adjustmentId: Long?
    ): BillingClientResources.Reply.PaidResult {

        val raw = this.localStore.find { it.billPayId == billPayId }!!

        raw.apply {
            val repayPayload = this.payPayload.run {
                DefaultPayPayload(
                    productCode,
                    topicId,
                    userId,
                    type,
                    this.adjustmentId,
                    bookOrderId,
                    itemName,
                    accountSettlementDate,
                    amountOfPay,
                    usingPayMethod,
                    preDefineValue
                )
            }
            this.payPayload = repayPayload
        }

        raw.billPayId = Random.nextLong()

        return BillingClientResources.Reply.PaidResult(
            raw.billPayId!!,
            amountOfPay,
            raw.methods, LocalDateTime.now()
        )
    }

    override fun cancelEntire(userId: Long, billPayId: Long, cause: String): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.billPayId == billPayId }!!
        raw.isCancel = true

        return BillingClientResources.Reply.CancelResult(
            billPayId,
            raw.methods,
            LocalDateTime.now()
        )
    }

    override fun cancelEntireWithPenalty(
        userId: Long,
        billPayId: Long,
        cause: String,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.billPayId == billPayId }!!
        raw.isCancel = true
        raw.refundAmount = raw.payPayload.amountOfPay - penaltyAmount
        raw.penaltyAmount = penaltyAmount

        return BillingClientResources.Reply.CancelResult(
            billPayId,
            raw.methods,
            LocalDateTime.now(),
            penaltyAmount
        )
    }

    override fun cancelPartial(
        userId: Long,
        billPayId: Long,
        cause: String,
        cancelAmount: Long
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.billPayId == billPayId }!!
        raw.isCancel = true
        raw.refundAmount = raw.payPayload.amountOfPay - cancelAmount

        return BillingClientResources.Reply.CancelResult(
            billPayId,
            raw.methods,
            LocalDateTime.now()
        )
    }

    override fun cancelPartialWithPenalty(
        userId: Long,
        billPayId: Long,
        cause: String,
        cancelAmount: Long,
        penaltyAmount: Long
    ): BillingClientResources.Reply.CancelResult {

        val raw = this.localStore.find { it.billPayId == billPayId }!!
        raw.isCancel = true
        raw.refundAmount = raw.payPayload.amountOfPay - (cancelAmount + penaltyAmount)
        raw.penaltyAmount = penaltyAmount

        return BillingClientResources.Reply.CancelResult(
            billPayId,
            raw.methods,
            LocalDateTime.now(),
            penaltyAmount
        )
    }

    override fun findByReceipt(productCode: ProductCode, userId: Long, billPayId: Long): PaidReceipt {
        val raw = this.localStore.find { it.billPayId == billPayId }!!
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
            if (raw.isCancel) raw.refundAmount else null,
            if (raw.isCancel) LocalDateTime.now() else null
        )
    }

    data class DummyLocalRaw(
        var userId: Long,
        var payPayload: PayPayload,
        var methods: List<BillingClientResources.Reply.MethodAmountPair>,
        var isCancel: Boolean = false,
        var billPayId: Long? = null,
        var refundAmount: Long? = null,
        var penaltyAmount: Long? = null
    )
}
