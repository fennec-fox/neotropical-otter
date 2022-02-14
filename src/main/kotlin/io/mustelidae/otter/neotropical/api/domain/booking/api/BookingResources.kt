package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Location
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.Item
import io.mustelidae.otter.neotropical.api.domain.booking.ItemOption
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class BookingResources {

    class Request {
        @Schema(name = "Booking.Request.PrePayBook")
        data class PrePayBook(
            val orderId: String,
            val payKey: String? = null,
            val point: Long? = null,
            val discountCoupon: Coupon? = null,
            val voucher: Voucher? = null
        )

        @Schema(name = "Booking.Request.PostPayBook")
        data class PostPayBook(
            val orderId: String,
            val payKey: String,
            val point: Long? = null,
            val discountCoupon: Coupon? = null,
            val voucher: Voucher? = null
        )

        @Schema(name = "Booking.Request.PostPayCompleteBook")
        data class PostPayCompleteBook(
            val changeAmount: Long? = null,
            val changeAdjustmentId: Long? = null,
            val cause: String? = null,
            val changePaymentMethod: UsingPayMethod? = null
        )

        @Schema(name = "Booking.Request.Voucher")
        data class Voucher(
            val id: Long,
            val groupId: Long? = null
        )

        @Schema(name = "Booking.Request.Coupon")
        data class Coupon(
            val id: Long,
            val groupId: Long? = null
        )

        companion object
    }

    class Reply {

        @Schema(name = "Booking.Reply.BookingOfOrdered")
        data class BookingOfOrdered(
            val id: Long,
            val createdAt: LocalDateTime,
            val modifiedAt: LocalDateTime,
            val userId: Long,
            val productCode: ProductCode,
            val topicId: String,
            val orderId: String,
            val title: String,
            val status: Booking.Status,
            val description: String? = null,
            val reservationDate: LocalDateTime? = null,
            val price: Long? = null,
            val verticalId: Long? = null,
            val contents: List<SimpleContent>? = null,
            val location: Location? = null
        ) {
            companion object {
                fun from(booking: Booking): BookingOfOrdered {
                    return booking.run {
                        BookingOfOrdered(
                            id!!,
                            createdAt!!,
                            modifiedAt!!,
                            userId,
                            productCode,
                            topicId,
                            orderId,
                            title,
                            status,
                            description,
                            reservationDate,
                            price,
                            verticalId,
                            getContent(),
                            getLocation()
                        )
                    }
                }
            }
        }

        @Schema(name = "Booking.Reply.BookingOfOrderedWithItems")
        data class BookingOfOrderedWithItems(
            val id: Long,
            val createdAt: LocalDateTime,
            val modifiedAt: LocalDateTime,
            val userId: Long,
            val productCode: ProductCode,
            val topicId: String,
            val orderId: String,
            val title: String,
            val status: Booking.Status,
            val description: String? = null,
            val reservationDate: LocalDateTime? = null,
            val price: Long? = null,
            val verticalId: Long? = null,
            val contents: List<SimpleContent>? = null,
            val location: Location? = null,
            val items: List<ItemOfOrdered>
        ) {

            companion object {
                fun from(booking: Booking): BookingOfOrderedWithItems {
                    val items = booking.items.map { ItemOfOrdered.from(it) }
                    return booking.run {
                        BookingOfOrderedWithItems(
                            id!!,
                            createdAt!!,
                            modifiedAt!!,
                            userId,
                            productCode,
                            topicId,
                            orderId,
                            title,
                            status,
                            description,
                            reservationDate,
                            price,
                            verticalId,
                            getContent(),
                            getLocation(),
                            items
                        )
                    }
                }
            }

            @Schema(name = "Booking.Reply.Booking.Item")
            data class ItemOfOrdered(
                val id: Long,
                val name: String,
                val status: Item.Status,
                val options: List<OptionOfOrdered>,
                val description: String? = null,
                val price: Long? = null,
                val discount: Long? = null,
                val verticalId: Long? = null,
                val canceledDate: LocalDateTime? = null
            ) {
                companion object {
                    fun from(item: Item): ItemOfOrdered {
                        val itemOptions = item.itemOptions.map { OptionOfOrdered.from(it) }
                        return item.run {
                            ItemOfOrdered(
                                id!!,
                                name,
                                status,
                                itemOptions,
                                description,
                                price,
                                discount,
                                verticalId,
                                canceledDate
                            )
                        }
                    }
                }

                @Schema(name = "Booking.Reply.Booking.Item.Option")
                data class OptionOfOrdered(
                    val id: Long,
                    val name: String,
                    var description: String? = null,
                    val price: Long? = null
                ) {
                    companion object {
                        fun from(itemOption: ItemOption): OptionOfOrdered {
                            return itemOption.run {
                                OptionOfOrdered(id!!, name, description, price)
                            }
                        }
                    }
                }
            }
        }
    }
}
