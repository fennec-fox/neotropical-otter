package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

class UserPermission(
    private val id: Long
) : Permission {
    private var valid: Boolean = true

    override fun checkOrderSheet(orderSheet: OrderSheet): Boolean {
        val isOk = (id == orderSheet.userId)
        valid = isOk && valid
        return isOk
    }

    override fun isValid(): Boolean = valid
}
