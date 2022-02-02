package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

class AdminPermission(
    private val id: Long
) : Permission {
    override fun checkOrderSheet(orderSheet: OrderSheet): Boolean = true
    override fun isValid(): Boolean = true
}
