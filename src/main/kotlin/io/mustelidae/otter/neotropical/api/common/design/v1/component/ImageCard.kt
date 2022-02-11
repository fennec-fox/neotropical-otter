package io.mustelidae.otter.neotropical.api.common.design.v1.component

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.Component.ImageCard")
class ImageCard(
    val order: Int,
    val title: String? = null,
    val images: List<Image>
) {
    @Schema(name = "Design.V1.Component.ImageCard.Image")
    data class Image(
        val order: Int,
        val originalUrl: String,
        val thumbnailUrl: String? = null,
        val description: String? = null
    )
}
