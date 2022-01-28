package io.mustelidae.otter.neotropical.api.domain.order

import org.springframework.validation.AbstractErrors
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import java.util.HashMap
import java.util.LinkedList

class OrderFormSpecificationErrors : AbstractErrors() {

    private val errors: MutableList<ObjectError> = mutableListOf()
    private val fieldValues = HashMap<String, Any>()

    override fun getObjectName(): String = "Checkout Request variable error"

    override fun reject(errorCode: String, errorArgs: Array<out Any>?, defaultMessage: String?) {
        errors.add(ObjectError(objectName, arrayOf(errorCode), errorArgs, defaultMessage))
    }

    override fun rejectValue(field: String?, errorCode: String, errorArgs: Array<out Any>?, defaultMessage: String?) {
        if (nestedPath.isBlank() && field.isNullOrBlank()) {
            reject(errorCode, errorArgs, defaultMessage)
            return
        } else {
            val fixedField = fixedField(field)
            val newVal: Any? = fieldValues[field]
            val fe = FieldError(
                objectName, fixedField, newVal, false, arrayOf(errorCode), errorArgs, defaultMessage
            )
            this.errors.add(fe)
        }
    }

    override fun addAllErrors(errors: Errors) {
        require(errors.objectName == objectName) { "Errors object needs to have same object name" }
        this.errors.addAll(errors.allErrors)
    }

    override fun getGlobalErrors(): MutableList<ObjectError> {
        val result: MutableList<ObjectError> = LinkedList()
        for (objectError in errors) {
            if (objectError !is FieldError) {
                result.add(objectError)
            }
        }
        return result
    }

    override fun getFieldErrors(): MutableList<FieldError> {
        val result: MutableList<FieldError> = LinkedList()
        for (objectError in errors) {
            if (objectError is FieldError) {
                result.add(objectError)
            }
        }
        return result
    }

    override fun getFieldValue(field: String): Any? {
        val fieldError = getFieldError(field)
        return if (fieldError != null) {
            fieldError.rejectedValue
        } else {
            fieldValues[field]
        }
    }
}
