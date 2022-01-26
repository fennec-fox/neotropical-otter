package io.mustelidae.otter.neotropical.api.domain.payment.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import java.time.LocalDate

interface PayPayload {
    val productCode: ProductCode
    val topicId: String
    val userId: Long
    val type: PayType
    val adjustmentId: Long
    val paymentOrderId: String
    val itemId: Long
    val itemName: String
    val accountSettlementDate: LocalDate
    val amountOfPay: Long
    val amountOfPoint: Long
    val discountCouponId: Long?
    val payKey: String?
    val preDefineValue: MutableMap<String, Any>?
}
