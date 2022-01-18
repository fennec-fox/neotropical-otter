package io.mustelidae.otter.neotropical.api.common.design.v1.component

class PolicyCard(
    val order: Int,
    val title: String,
    val explanations: List<Explanation>,
    val isFold: Boolean = false
) {
    data class Explanation(
        val order: Int,
        val title: String? = null,
        val description: String
    )
}
