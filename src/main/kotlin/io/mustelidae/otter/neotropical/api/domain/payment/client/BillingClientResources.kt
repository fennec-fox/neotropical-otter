package io.mustelidae.otter.neotropical.api.domain.payment.client

import io.mustelidae.otter.neotropical.api.common.ErrorSource
import io.mustelidae.otter.neotropical.api.domain.payment.PaymentMethod
import java.time.LocalDateTime

class BillingClientResources {

    class Request

    class Reply {
        data class PaidResult(
            val paymentId: Long,
            val amountOfPaid: Long,
            val paidMethods: List<MethodAmountPair>,
            val transactionDate: LocalDateTime,
        )

        data class CancelResult(
            val paymentId: Long,
            val refundMethods: List<MethodAmountPair>,
            val transactionDate: LocalDateTime,
            val penaltyAmount: Long? = null
        )

        data class MethodAmountPair(
            val method: PaymentMethod,
            val amount: Long
        )

        data class BillingError(
            override val code: String,
            override val message: String,
            override var causeBy: Map<String, Any?>?,
            override var refCode: String?
        ) : ErrorSource {
            var pgMessage: String? = null
        }
    }
}
