package io.mustelidae.otter.neotropical.api.domain.vertical

data class CallOffBooking(
    val isPossible: Boolean,
    val estimateRefundAmount: Long? = null,
    val estimatePenaltyAmount: Long? = null,
)
