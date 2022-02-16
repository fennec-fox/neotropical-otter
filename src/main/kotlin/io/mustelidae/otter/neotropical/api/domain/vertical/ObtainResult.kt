package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard

class ObtainResult(
    override val isSuccess: Boolean,
    val onAutoConfirm: Boolean,
    override val failCause: String? = null,
    val pairingIds: List<PairOfBookingAndVertical>? = null,
    val policyCards: List<PolicyCard>? = null
) : ExchangeResult(isSuccess, failCause) {

    data class PairOfBookingAndVertical(
        val bookingId: Long,
        val verticalId: Long
    )
}
