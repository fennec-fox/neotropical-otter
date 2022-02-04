package io.mustelidae.otter.neotropical.api.domain.payment.client.voucher

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.method.pay.Voucher

class DummyVoucherClient: VoucherClient {
    override fun use(userId: Long, productCode: ProductCode, topicId: String, voucher: Voucher) {}

    override fun cancel(userId: Long, voucherId: Long) {}
}