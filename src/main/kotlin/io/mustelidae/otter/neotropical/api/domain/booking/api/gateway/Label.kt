package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway

data class Label(
    val name: String,
    val isStrong: Boolean = false,
    val color: Color? = null
) {
    enum class Color {
        RED, BLUE, GREEN, GRAY, YELLOW, ORANGE
    }
}
