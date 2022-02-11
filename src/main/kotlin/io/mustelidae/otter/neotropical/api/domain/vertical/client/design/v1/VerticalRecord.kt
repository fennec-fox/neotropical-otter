package io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1

import io.mustelidae.otter.neotropical.api.common.design.v1.component.Label
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.Vertical.VerticalRecord")
class VerticalRecord(
    val id: Long,
    val bookingId: Long,
    val status: Label,
    val recordCard: RecordCard,
    val preDefineField: Map<String, Any?>?
)
