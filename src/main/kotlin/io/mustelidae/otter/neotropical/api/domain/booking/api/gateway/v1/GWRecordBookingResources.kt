package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.ApiModelType
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.Label
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import io.mustelidae.otter.neotropical.api.domain.payment.PaymentMethod
import java.time.LocalDateTime

class GWRecordBookingResources {

    class Reply {

        data class Record(
            val id: Long,
            val productCode: ProductCode,
            val topicId: String,
            val lastUpdatedAt: LocalDateTime,
            val title: String,
            val type: ApiModelType,
            val status: Label,
            val landingType: LandingWay,
            val isActive: Boolean = true,
            val landingPath: String? = null,
            val contents: List<SimpleContent> = emptyList(),
            val receipt: SimpleReceipt
        ) {

            data class SimpleReceipt(
                val payMethods: List<PaymentMethod>,
                val status: Label,
                val amountOfPay: Long? = null,
                val amountOfRefund: Long? = null,
                val paymentId: Long? = null
            )
        }

        data class RecordDetail(
            val id: Long,
            val createAt: LocalDateTime,
            val lastUpdatedAt: LocalDateTime,
            val topicId: String,
        )
    }
}
