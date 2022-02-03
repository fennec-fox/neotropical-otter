package io.mustelidae.otter.neotropical.api.common.method.pay

import com.fasterxml.jackson.annotation.JsonIgnore

class Voucher(
    val id: Long,
    val groupId: Long,
) : PayMethod {
    @JsonIgnore
    override val paymentMethod: PaymentMethod = PaymentMethod.VOUCHER
}
