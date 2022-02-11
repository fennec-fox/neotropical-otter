package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ErrorSource
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaymentMethod
import java.time.LocalDateTime

class BillingClientResources {

    class Request

    class Reply {
        data class PaidResult(
            val billPayId: Long,
            val amountOfPaid: Long,
            val paidMethods: List<MethodAmountPair>,
            val transactionDate: LocalDateTime,
        )

        data class CancelResult(
            val billPayId: Long,
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

        data class CardDetail(
            val id: Long,
            val payKey: String,
            val name: String,
            val number: String,
            val corp: String,
            val isPrimary: Boolean,
            val status: Boolean,
            val nickName: String? = null,
            val usingProductCode: ProductCode? = null
        )

        data class CouponDetail(
            val id: Long,
            val name: String,
            val discountAmount: Long,
            val status: Boolean,
            val condition: String? = null,
            val startDate: LocalDateTime? = null,
            val endDate: LocalDateTime? = null,
            val groupId: Long? = null,
        )
    }
}
