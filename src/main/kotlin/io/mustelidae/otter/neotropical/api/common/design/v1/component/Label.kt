package io.mustelidae.otter.neotropical.api.common.design.v1.component

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.Component.Label")
data class Label(
    val name: String,
    val isStrong: Boolean = false,
    val color: Color? = null
) {
    enum class Color {
        RED, BLUE, GREEN, GRAY, YELLOW, ORANGE
    }
}
