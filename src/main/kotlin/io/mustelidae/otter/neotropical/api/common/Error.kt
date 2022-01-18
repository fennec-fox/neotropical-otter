package io.mustelidae.otter.neotropical.api.common

class Error(
    errorCode: ErrorCode,
    override val message: String,
    override var causeBy: Map<String, Any?>? = null
) : ErrorSource {
    override val code = errorCode.name
    override var refCode: String? = null
}
