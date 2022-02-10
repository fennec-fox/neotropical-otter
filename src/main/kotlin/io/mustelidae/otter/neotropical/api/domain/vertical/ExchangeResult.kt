package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard

data class ExchangeResult(
    val isSuccess: Boolean,
    val failCause: String? = null,
    val pairingIds: List<PairOfBookingAndVertical>? = null,
    val policyCards: List<PolicyCard>? = null
) {
    data class PairOfBookingAndVertical(
        val bookingId: Long,
        val verticalId: Long
    )
}
