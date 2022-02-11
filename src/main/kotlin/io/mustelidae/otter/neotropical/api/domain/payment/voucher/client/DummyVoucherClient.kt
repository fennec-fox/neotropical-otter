package io.mustelidae.otter.neotropical.api.domain.payment.voucher.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher

class DummyVoucherClient : VoucherClient {
    override fun findById(userId: Long, voucherId: Long, groupId: Long?): VoucherClientResources.Reply.Detail {
        return VoucherClientResources.Reply.Detail(
            voucherId,
            ProductCode.MOCK_UP,
            "VOUCHER-MOCK-23421525w77",
            "Test voucher",
            "test",
            VoucherClientResources.Reply.Detail.Status.BEFORE_USE
        )
    }

    override fun findById(userId: Long, voucherId: Long): VoucherClientResources.Reply.Detail {
        return VoucherClientResources.Reply.Detail(
            voucherId,
            ProductCode.MOCK_UP,
            "VOUCHER-MOCK-23421525w77",
            "Test voucher",
            "test",
            VoucherClientResources.Reply.Detail.Status.USED
        )
    }

    override fun use(userId: Long, productCode: ProductCode, topicId: String, voucher: Voucher) {}

    override fun cancel(userId: Long, voucherId: Long) {}
}
