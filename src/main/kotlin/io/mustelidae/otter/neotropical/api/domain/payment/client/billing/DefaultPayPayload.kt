package io.mustelidae.otter.neotropical.api.domain.payment.client.billing

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.method.pay.UsingPayMethod
import java.time.LocalDate

class DefaultPayPayload(
    override val productCode: ProductCode,
    override val topicId: String,
    override val userId: Long,
    override val type: PayType,
    override val adjustmentId: Long,
    override val paymentOrderId: String,
    override val itemName: String,
    override val accountSettlementDate: LocalDate,
    override val amountOfPay: Long,
    override val usingPayMethod: UsingPayMethod,
    override val preDefineValue: Map<String, Any>?
) : PayPayload
