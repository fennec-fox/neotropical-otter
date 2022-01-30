package io.mustelidae.otter.neotropical.api.common.design.v1.component

data class DisplayCard(
    val order: Int,
    val title: String? = null,
    val subTitle: String? = null,
    val contents: List<Content> = emptyList(),
) {
    data class Content(
        val name: String,
        val type: Type,
        var box: ContentBox
    ) {
        enum class Type {
            TEXT, IMAGES, LINK, CALL
        }
        data class ContentBox(
            val text: String? = null,
            val image: List<Image>? = null,
            val link: Link? = null,
            val call: Call? = null,
        ) {
            data class Image(
                val order: Int,
                val originalUrl: String,
                val thumbnailUrl: String? = null,
            )

            data class Call(
                val name: String,
                val phone: String
            )

            data class Link(
                val name: String,
                val url: String,
            )
        }
    }
}
