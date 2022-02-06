package io.mustelidae.otter.neotropical.api.domain.vertical

class CancellationUnit(
    val userId: Long,
    val cancelBooks: List<CancelBook>,
) {
    data class CancelBook(
        val bookingId: Long,
        val itemIds: List<Long>? = null
    )
}
