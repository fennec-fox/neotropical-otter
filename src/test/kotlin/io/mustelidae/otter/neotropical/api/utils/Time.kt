package io.mustelidae.otter.neotropical.api.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toISO(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
}
