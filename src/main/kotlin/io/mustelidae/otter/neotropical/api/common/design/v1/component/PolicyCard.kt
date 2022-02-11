package io.mustelidae.otter.neotropical.api.common.design.v1.component

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.Component.PolicyCard")
class PolicyCard(
    val order: Int,
    val title: String,
    val explanations: List<Explanation>,
    val isFold: Boolean = false,
    val showOnlyAdmin: Boolean,
    val version: Int = 1
) {
    @Schema(name = "Design.V1.Component.PolicyCard.Explanation")
    data class Explanation(
        val order: Int,
        val title: String? = null,
        val description: String
    )
}
