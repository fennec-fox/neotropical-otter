package io.mustelidae.otter.neotropical.api.common.design.v1.component

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.Component.DisplayCard")
data class DisplayCard(
    val order: Int,
    val title: String? = null,
    val subTitle: String? = null,
    val contents: List<Content> = emptyList(),
) {
    @Schema(name = "Design.V1.Component.DisplayCard.Content")
    data class Content(
        val name: String,
        val type: Type,
        var box: ContentBox
    ) {
        enum class Type {
            TEXT, IMAGES, LINK, CALL
        }
        @Schema(name = "Design.V1.Component.DisplayCard.Content.ContentBox")
        data class ContentBox(
            val text: String? = null,
            val image: List<Image>? = null,
            val link: Link? = null,
            val call: Call? = null,
        ) {
            @Schema(name = "Design.V1.Component.DisplayCard.Content.ContentBox.Image")
            data class Image(
                val order: Int,
                val originalUrl: String,
                val thumbnailUrl: String? = null,
            )

            @Schema(name = "Design.V1.Component.DisplayCard.Content.ContentBox.Call")
            data class Call(
                val name: String,
                val phone: String
            )

            @Schema(name = "Design.V1.Component.DisplayCard.Content.ContentBox.Link")
            data class Link(
                val name: String,
                val url: String,
            )
        }
    }
}
