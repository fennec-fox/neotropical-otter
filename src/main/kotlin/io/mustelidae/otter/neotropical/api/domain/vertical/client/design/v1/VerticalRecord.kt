package io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1

import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.Label

class VerticalRecord(
    val id: Long,
    val bookingId: Long,
    val status: Label,
    val recordCard: RecordCard,
    val preDefineField: Map<String, Any?>?
)
