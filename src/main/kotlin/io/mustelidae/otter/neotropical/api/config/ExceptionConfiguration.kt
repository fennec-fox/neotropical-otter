@file:Suppress("unused")

package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.common.ErrorSource
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.core.env.Environment
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.PRECONDITION_FAILED
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(annotations = [RestController::class])
class ExceptionConfiguration(
    private val env: Environment
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * the developer does not know about.
     */
    @ExceptionHandler(value = [RuntimeException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGlobalException(e: RuntimeException, request: HttpServletRequest): Map<String, Any> {
        log.error("Unexpected error", e)
        return errorForm(request, e, Error(ErrorCode.S000, "Oops, something went wrong."))
    }

    @ExceptionHandler(value = [InvalidDataAccessApiUsageException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleInvalidDataAccessApiUsageException(e: InvalidDataAccessApiUsageException, request: HttpServletRequest): Map<String, Any> {
        return errorForm(request, e, Error(ErrorCode.SI02, e.message!!))
    }

    /**
     * policy is not defined.
     */
    @ExceptionHandler(value = [IllegalStateException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleIllegalStateException(e: IllegalStateException, request: HttpServletRequest): Map<String, Any> {
        return errorForm(request, e, Error(ErrorCode.P000, "Oops, something went wrong."))
    }

    /**
     * Development mistakes declared by developers
     */
    @ExceptionHandler(value = [DevelopMistakeException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleDevelopMistakeException(e: DevelopMistakeException, request: HttpServletRequest): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    /**
     * Invalid input
     */
    @ExceptionHandler(value = [IllegalArgumentException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun handleIllegalArgumentException(e: IllegalArgumentException, request: HttpServletRequest): Map<String, Any> {
        log.error("[T] wrong input.", e)
        return errorForm(request, e, Error(ErrorCode.HI01, "Invalid input"))
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): MutableMap<String, Any> {
        return errorForm(
            request,
            e,
            Error(
                ErrorCode.HI00,
                e.bindingResult.fieldError?.defaultMessage ?: run {
                    ErrorCode.HI00.summary
                }
            )
        )
    }

    @ExceptionHandler(value = [CommunicationException::class])
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleCommunicationException(
        e: CommunicationException,
        request: HttpServletRequest
    ): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    /**
     * human error
     */
    @ExceptionHandler(value = [HumanException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun handleHumanException(
        e: HumanException,
        request: HttpServletRequest
    ): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    /**
     * application policy
     */
    @ExceptionHandler(value = [PolicyException::class])
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    fun policyException(
        e: PolicyException,
        request: HttpServletRequest
    ): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    /**
     * unauthorized
     */
    @ExceptionHandler(value = [UnAuthorizedException::class])
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    fun unAuthorizedException(
        e: UnAuthorizedException,
        request: HttpServletRequest
    ): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    /**
     * data not found
     */
    @ExceptionHandler(value = [DataNotFindException::class])
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    fun dataNotFindException(
        e: DataNotFindException,
        request: HttpServletRequest
    ): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    @ExceptionHandler(value = [PreconditionFailException::class])
    @ResponseStatus(PRECONDITION_FAILED)
    @ResponseBody
    fun preconditionFailException(
        e: PreconditionFailException,
        request: HttpServletRequest
    ): Map<String, Any> {
        return errorForm(request, e, e.error)
    }

    private fun errorForm(request: HttpServletRequest, e: Exception, error: ErrorSource): MutableMap<String, Any> {

        val errorAttributeOptions = if (env.activeProfiles.contains("prod").not())
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE)
        else ErrorAttributeOptions.defaults()

        val errorAttributes =
            DefaultErrorAttributes().getErrorAttributes(ServletWebRequest(request), errorAttributeOptions)

        errorAttributes.apply {
            this["message"] = error.message
            this["code"] = error.code
            this["causeBy"] = error.causeBy
            this["type"] = e.javaClass.simpleName
        }

        return errorAttributes
    }

    @Suppress(
        "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
        "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
    )
    private fun methodArgumentNotValidExceptionErrorForm(errors: List<FieldError>) =
        errors.map {
            ValidationError(
                field = it.field,
                rejectedValue = it.rejectedValue.toString(),
                message = it.defaultMessage
            )
        }.toList()

    private data class ValidationError(
        val field: String,
        val rejectedValue: String,
        val message: String?
    )
}
