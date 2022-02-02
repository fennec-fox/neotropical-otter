package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

class PartnerPermission(
    private val id: Long
) : Permission {
    private var valid: Boolean = true
    override fun checkOrderSheet(orderSheet: OrderSheet): Boolean {
        return false
    }

    override fun isValid(): Boolean = valid
}
