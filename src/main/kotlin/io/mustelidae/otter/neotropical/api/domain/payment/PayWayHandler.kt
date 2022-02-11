package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient
import org.springframework.stereotype.Service

@Service
class PayWayHandler(
    private val voucherClient: VoucherClient,
    private val billingPayClient: BillingPayClient
) {

    fun getPrePayWay(userId: Long, amountOfPay: Long, voucher: Voucher?): PayWay {
        if (voucher != null)
            return VoucherPayWay(userId, amountOfPay, voucherClient)

        if (amountOfPay == 0L)
            return FreePayWay(userId, amountOfPay)

        return BillingPrePayWay(userId, amountOfPay, billingPayClient)
    }

    fun getPostPayWay(userId: Long, amountOfPay: Long): PayWay {
        if (amountOfPay == 0L)
            return FreePayWay(userId, amountOfPay)
        return BillingPostPayWay(userId, amountOfPay, billingPayClient)
    }

    fun getPostPayWay(payment: Payment, amountOfPay: Long? = null, voucher: Voucher?): PayWay {
        if (voucher != null)
            return VoucherPayWay(payment, voucherClient)

        val amount = amountOfPay ?: payment.priceOfOrder

        if (amount == 0L)
            return FreePayWay(payment)
        return BillingPostPayWay(payment, billingPayClient)
    }
}
