package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.common.method.pay.Voucher
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPayClient
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
}
