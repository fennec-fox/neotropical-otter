package io.mustelidae.otter.neotropical.api.common

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Common.Vehicle")
data class Vehicle(
    val gradeId: Long,
    val name: String,
    val plateNumber: String
)
