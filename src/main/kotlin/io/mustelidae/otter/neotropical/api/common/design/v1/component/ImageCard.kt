package io.mustelidae.otter.neotropical.api.common.design.v1.component

class ImageCard(
    val order: Int,
    val title: String? = null,
    val images: List<Image>
) {
    data class Image(
        val order: Int,
        val originalUrl: String,
        val thumbnailUrl: String? = null,
        val description: String? = null
    )
}
