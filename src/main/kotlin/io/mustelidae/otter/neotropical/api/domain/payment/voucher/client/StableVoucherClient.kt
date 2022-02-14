package io.mustelidae.otter.neotropical.api.domain.payment.voucher.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher

class StableVoucherClient : VoucherClient {
    override fun findById(userId: Long, voucherId: Long, groupId: Long?): VoucherClientResources.Reply.Detail {
        TODO("Not yet implemented")
    }

    override fun findById(userId: Long, voucherId: Long): VoucherClientResources.Reply.Detail {
        TODO("Not yet implemented")
    }

    override fun reserveUse(userId: Long, voucher: Voucher) {
        TODO("Not yet implemented")
    }

    override fun rollbackReserve(userId: Long, voucher: Voucher) {
        TODO("Not yet implemented")
    }

    override fun use(userId: Long, productCode: ProductCode, topicId: String, voucher: Voucher) {
        TODO("Not yet implemented")
    }

    override fun cancel(userId: Long, voucherId: Long) {
        TODO("Not yet implemented")
    }
}
