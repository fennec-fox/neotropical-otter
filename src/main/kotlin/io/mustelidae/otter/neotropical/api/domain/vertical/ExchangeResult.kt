package io.mustelidae.otter.neotropical.api.domain.vertical

open class ExchangeResult(
    open val isSuccess: Boolean,
    open val failCause: String? = null,
)
