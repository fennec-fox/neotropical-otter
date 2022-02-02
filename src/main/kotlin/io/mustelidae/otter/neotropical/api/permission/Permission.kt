package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

interface Permission {

    fun checkOrderSheet(orderSheet: OrderSheet): Boolean
    fun isValid(): Boolean
}
