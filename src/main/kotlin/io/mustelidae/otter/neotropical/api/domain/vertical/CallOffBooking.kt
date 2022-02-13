package io.mustelidae.otter.neotropical.api.domain.vertical

data class CallOffBooking(
    val isPossible: Boolean,
    val cancelFee: Long,
    val estimateRefundAmount: Long? = null,
    val estimatePenaltyAmount: Long? = null,
    val impossibleReason: String? = null,
) {
    fun hasPenalty(): Boolean = (cancelFee != 0L)
}
