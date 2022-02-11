package io.mustelidae.otter.neotropical.api.domain.payment.voucher

import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient
import org.springframework.stereotype.Service

@Service
class VoucherFinder(
    private val voucherClient: VoucherClient
) {
    fun findOne(userId: Long, id: Long): Voucher {
        val reply = voucherClient.findById(userId, id)
        return Voucher(
            reply.id,
            reply.groupId,
        ).apply {
            name = reply.name
            description = reply.description
        }
    }
}
