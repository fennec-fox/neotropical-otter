package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.common.design.v1.component.DisplayCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.ImageCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.Label
import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.common.method.PaidCreditCard
import io.mustelidae.otter.neotropical.api.common.method.PaidDiscountCoupon
import io.mustelidae.otter.neotropical.api.common.method.PaidPoint
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.api.BookingResources
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.ApiModelType
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingPage
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.payment.PaidReceipt
import io.mustelidae.otter.neotropical.api.domain.payment.Payment
import io.mustelidae.otter.neotropical.api.domain.payment.method.PaymentMethod
import io.mustelidae.otter.neotropical.api.domain.payment.method.Voucher
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class GWRecordBookingResources {

    class Reply {

        @Schema(name = "Gateway.Reply.Record")
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
            @Schema(name = "Gateway.Reply.Record.SimpleReceipt")
            data class SimpleReceipt(
                val payMethods: List<PaymentMethod>,
                val status: Label,
                val amountOfPay: Long? = null,
                val amountOfRefund: Long? = null,
                val billPayId: Long? = null,
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
                                billPayId,
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

        @Schema(name = "Gateway.Reply.RecordDetail")
        data class RecordDetail(
            val id: Long,
            val createAt: LocalDateTime,
            val productCode: ProductCode,
            val topicId: String,
            val lastUpdatedAt: LocalDateTime,
            val title: String,
            val type: ApiModelType,
            val status: Label,
            val items: List<BookingResources.Reply.BookingOfOrderedWithItems.ItemOfOrdered>,
            val landingType: LandingWay,
            val isActive: Boolean = true,
            val landingPath: String? = null,
            val contents: List<SimpleContent> = emptyList(),
            val receipt: PaymentReceipt,
            val verticalDetail: Detail,
            val productDefineField: Map<String, Any?>? = null
        ) {

            companion object {
                fun from(
                    orderSheet: OrderSheet,
                    booking: Booking,
                    verticalRecord: VerticalRecord,
                    landingPage: LandingPage,
                    paidReceipt: PaidReceipt?,
                    voucher: Voucher?
                ): RecordDetail {
                    return booking.run {
                        val items = booking.items.map { BookingResources.Reply.BookingOfOrderedWithItems.ItemOfOrdered.from(it) }
                        val color = when (booking.status) {
                            Booking.Status.WAIT -> Label.Color.YELLOW
                            Booking.Status.BOOKED -> Label.Color.BLUE
                            Booking.Status.COMPLETED -> Label.Color.GRAY
                            Booking.Status.CANCELED -> Label.Color.RED
                        }

                        RecordDetail(
                            id!!,
                            createdAt!!,
                            productCode,
                            topicId,
                            modifiedAt!!,
                            title,
                            ApiModelType.BOOKING,
                            Label(status.text, false, color),
                            items,
                            landingPage.getLandingWay(),
                            !isHide,
                            landingPage.getRecordDetail(),
                            getContent() ?: emptyList(),
                            PaymentReceipt.from(booking.payment!!, paidReceipt, voucher),
                            Detail.from(orderSheet, verticalRecord),
                            verticalRecord.preDefineField
                        )
                    }
                }
            }

            @Schema(name = "Gateway.Reply.RecordDetail.VerticalDetail")
            data class Detail(
                val displayCards: List<DisplayCard>? = null,
                val imageCards: List<ImageCard>? = null,
                val policyCards: List<PolicyCard>? = null
            ) {
                companion object {
                    fun from(orderSheet: OrderSheet, verticalRecord: VerticalRecord): Detail {
                        var policyCards = orderSheet.policyCapture?.snapShotPolicyCards

                        if (policyCards == null)
                            policyCards = verticalRecord.recordCard.policyCard

                        return Detail(
                            verticalRecord.recordCard.displayCards,
                            verticalRecord.recordCard.imageCards,
                            policyCards
                        )
                    }
                }
            }

            @Schema(name = "Gateway.Reply.RecordDetail.PaymentReceipt")
            data class PaymentReceipt(
                val totalPaidAmount: Long,
                val paidDate: LocalDateTime? = null,
                val creditCard: PaidCreditCard? = null,
                val point: PaidPoint? = null,
                val discountCoupon: PaidDiscountCoupon? = null,
                val voucher: Voucher? = null,

                val totalRefundAmount: Long? = null,
                val canceledDate: LocalDateTime? = null,
            ) {
                companion object {
                    fun from(payment: Payment, paidReceipt: PaidReceipt?, voucher: Voucher?): PaymentReceipt {

                        @Suppress("IfThenToElvis")
                        return if (paidReceipt != null) {
                            paidReceipt.run {
                                PaymentReceipt(
                                    paidAmount,
                                    paidDate,
                                    creditCard,
                                    point,
                                    discountCoupon,
                                    voucher,
                                    refundAmount,
                                    canceledDate
                                )
                            }
                        } else {
                            payment.run {
                                PaymentReceipt(
                                    paidAmount ?: 0,
                                    paidDate,
                                    null,
                                    null,
                                    null,
                                    voucher,
                                    refundAmount,
                                    cancelledDate
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
