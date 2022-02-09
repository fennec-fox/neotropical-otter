package io.mustelidae.otter.neotropical.api.domain.payment.voucher.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.method.pay.Voucher

class StableVoucherClient : VoucherClient {
    override fun use(userId: Long, productCode: ProductCode, topicId: String, voucher: Voucher) {
        // TODO("Not yet implemented")
    }

    override fun cancel(userId: Long, voucherId: Long) {
        // TODO("Not yet implemented")
    }
}
