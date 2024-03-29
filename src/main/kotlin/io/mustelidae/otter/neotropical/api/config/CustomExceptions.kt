package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.common.ErrorSource
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.Payment

open class CustomException(val error: ErrorSource) : RuntimeException(error.message)

/**
 * Human Exception
 */
open class HumanException(error: ErrorSource) : CustomException(error)
class DataNotFindException : HumanException {
    constructor(message: String) : super(Error(ErrorCode.HD00, message))
    constructor(id: String, message: String) : super(Error(ErrorCode.HD00, message, mapOf("id" to id)))
    constructor(id: Long, message: String) : this(id.toString(), message)
}

class PreconditionFailException(message: String) : HumanException(Error(ErrorCode.HD02, message))

// ignore in sentry
class DataNotSearchedException : HumanException {
    constructor(message: String) : super(Error(ErrorCode.HD01, message))
    constructor(source: String, message: String) : super(Error(ErrorCode.HD01, message, mapOf("search source" to source)))
    constructor(id: Long, message: String) : this(id.toString(), message)
}

class MissingRequestXHeaderException(headerName: String) : HumanException(Error(ErrorCode.HI02, "Missing request header $headerName"))

class InvalidArgumentException(message: String) : HumanException(Error(ErrorCode.HI01, message))

open class SystemException(error: ErrorSource) : CustomException(error)
class DevelopMistakeException : SystemException {
    constructor(errorCode: ErrorCode) : super(Error(errorCode, errorCode.summary))
    constructor(message: String, causeBy: Map<String, Any?>? = null) : super(Error(ErrorCode.PD01, message, causeBy))
}

open class CommunicationException(error: ErrorSource) : CustomException(error)
class ClientException(target: String, message: String, code: String? = null) : CommunicationException(
    Error(
        ErrorCode.C000,
        message,
        causeBy = mapOf(
            "target" to target,
            "clientErrorCode" to code
        )
    ).apply {
        refCode = code
    }
)
class ConnectionTimeoutException(target: String, timeoutConfig: Int, url: String) : CommunicationException(
    Error(
        ErrorCode.CT01,
        "$target Connection fail",
        causeBy = mapOf(
            "target" to target,
            "url" to url,
            "timeout" to timeoutConfig
        )
    )
)

class ReadTimeoutException(target: String, timeoutConfig: Int, url: String) : CommunicationException(
    Error(
        ErrorCode.CT02,
        "$target Data not Received",
        causeBy = mapOf(
            "target" to target,
            "url" to url,
            "timeout" to timeoutConfig
        )
    )
)

open class AsyncException(message: String, causeBy: Map<String, Any?>? = null) : CustomException(
    Error(
        ErrorCode.SA00,
        message, causeBy
    )
)

/**
 * Policy Exception
 */
open class PolicyException(error: ErrorSource) : CustomException(error)
class CheckoutTimeoutException(message: String) : PolicyException(
    Error(
        ErrorCode.PC02,
        message
    )
)
class HandshakeFailException : PolicyException {
    constructor(userId: Long, productCode: ProductCode, failMessage: String?) : super(
        Error(
            ErrorCode.CV01,
            failMessage ?: "There is a problem with the vertical system. Ordering is not possible.",
            causeBy = mapOf(
                "userId" to userId,
                "productCode" to productCode,
                "fail" to failMessage
            )
        )
    )
    constructor(userId: Long, payment: Payment, e: CommunicationException) : super(
        Error(
            ErrorCode.PL02,
            "The payment cancellation could not be processed properly. Please contact the customer center.",
            causeBy = mapOf(
                "PayError" to e.error.refCode,
                "userId" to userId,
                "billPayId" to payment.billPayId,
                "e" to e.message
            )
        )
    )

    constructor(userId: Long, payment: Payment, message: String?) : super(
        Error(
            ErrorCode.PL02,
            "The payment cancellation could not be processed properly. Please contact the customer center.",
            causeBy = mapOf(
                "userId" to userId,
                "bilPayId" to payment.billPayId,
                "payKey" to payment.payKey,
                "payType" to payment.payType,
                "e" to message
            )
        )
    )
}

/**
 * UnAuthorized Exception
 */
open class UnAuthorizedException(error: ErrorSource) : CustomException(error)

class PermissionException : UnAuthorizedException {
    constructor() : super(Error(ErrorCode.HA00, "Access denied"))
    constructor(message: String) : super(Error(ErrorCode.HA01, message))
}
