package io.mustelidae.otter.neotropical.api.common.design

import io.mustelidae.otter.neotropical.api.common.design.v1.component.Label
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Design.V1.SimpleContent")
data class SimpleContent(
    val name: Label,
    var values: List<String>
)
