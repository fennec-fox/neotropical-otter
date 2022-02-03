package io.mustelidae.otter.neotropical.api.domain.payment.client.voucher

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.method.pay.Voucher

interface VoucherClient {

    fun use(userId: Long, productCode: ProductCode, topicId: String, voucher: Voucher)

    fun cancel(userId: Long, voucherId: Long)
}
