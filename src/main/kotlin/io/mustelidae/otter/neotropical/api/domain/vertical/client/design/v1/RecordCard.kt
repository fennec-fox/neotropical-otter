package io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1

import io.mustelidae.otter.neotropical.api.common.design.v1.component.DisplayCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.ImageCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.Vertical.RecordCard")
data class RecordCard(
    val policyCard: List<PolicyCard>? = null,
    val displayCards: List<DisplayCard>? = null,
    val imageCards: List<ImageCard>? = null
)
