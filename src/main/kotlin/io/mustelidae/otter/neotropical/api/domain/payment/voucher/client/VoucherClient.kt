package io.mustelidae.otter.neotropical.api.domain.payment.voucher.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher

interface VoucherClient {

    fun findById(userId: Long, voucherId: Long, groupId: Long?): VoucherClientResources.Reply.Detail

    fun findById(userId: Long, voucherId: Long): VoucherClientResources.Reply.Detail

    fun reserveUse(userId: Long, voucher: Voucher)

    fun rollbackReserve(userId: Long, voucher: Voucher)

    fun use(userId: Long, productCode: ProductCode, topicId: String, voucher: Voucher)

    fun cancel(userId: Long, voucherId: Long)
}
