package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.common.design.v1.component.DisplayCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.ImageCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.common.method.pay.PaidCreditCard
import io.mustelidae.otter.neotropical.api.common.method.pay.PaidDiscountCoupon
import io.mustelidae.otter.neotropical.api.common.method.pay.PaidPoint
import io.mustelidae.otter.neotropical.api.common.method.pay.PaymentMethod
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.ApiModelType
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.Label
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingPage
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import io.mustelidae.otter.neotropical.api.domain.payment.Payment
import java.time.LocalDateTime

class GWRecordBookingResources {

    class Reply {

        data class Record(
            val id: Long,
            val createAt: LocalDateTime,
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
                val paymentId: Long? = null,
                val voucherId: Long? = null
            ) {
                companion object {
                    fun from(payment: Payment): SimpleReceipt {
                        return payment.run {
                            SimpleReceipt(
                                getMethods(),
                                Label(status.name),
                                paidAmount,
                                refundAmount,
                                paymentId,
                                voucherId
                            )
                        }
                    }
                }
            }

            companion object {
                fun from(booking: Booking, landingPage: LandingPage): Record {
                    return booking.run {
                        Record(
                            id!!,
                            createdAt!!,
                            productCode,
                            topicId,
                            modifiedAt!!,
                            title,
                            ApiModelType.BOOKING,
                            Label(status.text),
                            landingPage.getLandingWay(),
                            true,
                            landingPage.getRecordDetail(),
                            getContent()!!,
                            SimpleReceipt.from(payment!!)
                        )
                    }
                }
            }
        }

        data class RecordDetail(
            val id: Long,
            val createAt: LocalDateTime,
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
            val receipt: PaymentReceipt,
            val detail: Detail,
            val productDefineField: Map<String, Any?>? = null
        ) {
            data class Detail(
                val displayCards: List<DisplayCard>,
                val imageCards: List<ImageCard>,
                val policyCards: List<PolicyCard>
            )

            data class PaymentReceipt(
                val totalPaidAmount: Long,
                val paidDate: LocalDateTime,
                val creditCard: PaidCreditCard? = null,
                val point: PaidPoint? = null,
                val discountCoupon: PaidDiscountCoupon? = null,
                val voucherId: Long? = null,

                val totalRefundAmount: Long? = null,
                val canceledDate: LocalDateTime? = null,
            )
        }
    }
}
