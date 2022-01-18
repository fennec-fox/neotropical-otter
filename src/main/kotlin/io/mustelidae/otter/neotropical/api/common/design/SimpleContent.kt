package io.mustelidae.otter.neotropical.api.common.design

import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.Label

data class SimpleContent(
    val name: Label,
    var values: List<String>
)
