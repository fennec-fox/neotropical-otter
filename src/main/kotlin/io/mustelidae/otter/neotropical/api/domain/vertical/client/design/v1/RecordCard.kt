package io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1

import io.mustelidae.otter.neotropical.api.common.design.v1.component.DisplayCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.ImageCard

data class RecordCard(
    val displayCards: List<DisplayCard>? = null,
    val imageCards: List<ImageCard>? = null
)
