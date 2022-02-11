package io.mustelidae.otter.neotropical.api.domain.payment.method

import com.fasterxml.jackson.annotation.JsonIgnore
import io.mustelidae.otter.neotropical.api.domain.payment.voucher.client.VoucherClient

open class Voucher(
    open val id: Long,
    open val groupId: Long? = null,
) : PayMethod {
    open var name: String? = null
    open var description: String? = null

    @Transient
    override var isValid: Boolean = true
    @Transient
    override var paymentMethod: PaymentMethod = PaymentMethod.VOUCHER
        protected set

    @JsonIgnore
    fun fillUpDetail(userId: Long, voucherClient: VoucherClient) {
        val detail = voucherClient.findById(userId, id, groupId)
        this.name = detail.name
        this.description = detail.description
    }
}
