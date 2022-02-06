package io.mustelidae.otter.neotropical.api.domain.vertical

data class ExchangeResult(
    val isSuccess: Boolean,
    val failCause: String? = null,
    val pairingIds: List<PairOfBookingAndVertical>? = null
) {
    data class PairOfBookingAndVertical(
        val bookingId: Long,
        val verticalId: Long
    )
}
