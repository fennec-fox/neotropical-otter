package io.mustelidae.otter.neotropical.api.common

interface ErrorSource {
    val code: String
    val message: String
    var causeBy: Map<String, Any?>?
    var refCode: String?
}
