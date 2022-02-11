package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import java.time.LocalDate

interface PayPayload {
    val productCode: ProductCode
    val topicId: String
    val userId: Long
    val type: PayType
    val adjustmentId: Long
    val bookOrderId: String
    val itemName: String
    val accountSettlementDate: LocalDate
    val amountOfPay: Long
    val usingPayMethod: UsingPayMethod
    val preDefineValue: Map<String, Any>?
}
