package io.mustelidae.otter.neotropical.api.domain.order.api

import io.mustelidae.otter.neotropical.api.common.Privacy
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import java.time.LocalDateTime

class OrderResources {

    class Modify {
        data class Order(
            val status: OrderSheet.Status,
        )
    }

    class Reply {
        data class PurchaseOrder(
            val id: String,
            val createdAt: LocalDateTime,
            val userId: Long,
            val productCode: ProductCode,
            val topicId: String,
            val products: List<OrderedProduct>,
            val adjustmentId: Long,
            val status: OrderSheet.Status,
            val privacy: Privacy? = null,
            val modifiedAt: LocalDateTime? = null,
            val preDefineField: Map<String, Any?>? = null,
            val policyCards: List<PolicyCard>? = null
        ) {

            data class OrderedProduct(
                val title: String,
                val contents: List<SimpleContent>,
                val goods: List<OrderedGoods>?,
                val id: Long? = null,
                val price: Long? = null,
                val description: String? = null,
                val reservationDate: LocalDateTime? = null,
                val preDefineField: Map<String, Any?>? = null
            ) {

                data class OrderedGoods(
                    val name: String,
                    val quantity: Int,
                    val id: Long? = null,
                    val priceOfUnit: Long? = null,
                    val discountPriceOfUnit: Long? = null,
                    val description: String? = null,
                    val goodsOptions: List<OrderedOption>?
                ) {
                    data class OrderedOption(
                        val name: String,
                        var description: String? = null,
                        val price: Long? = null,
                        val id: Long? = null,
                    ) {
                        companion object {
                            fun from(goodsOption: OrderSheet.Product.Goods.GoodsOption): OrderedOption {
                                return goodsOption.run {
                                    OrderedOption(name, description, price, id)
                                }
                            }
                        }
                    }

                    companion object {
                        fun from(goods: OrderSheet.Product.Goods): OrderedGoods {
                            return goods.run {
                                OrderedGoods(
                                    name,
                                    quantity,
                                    id,
                                    priceOfUnit,
                                    discountPriceOfUnit,
                                    description,
                                    goodsOptions?.map { OrderedOption.from(it) }
                                )
                            }
                        }
                    }
                }

                companion object {
                    fun from(product: OrderSheet.Product): OrderedProduct {
                        return product.run {
                            OrderedProduct(
                                title,
                                contents,
                                goods?.map { OrderedProduct.OrderedGoods.from(it) },
                                id,
                                price,
                                description,
                                reservationDate,
                                preDefineField
                            )
                        }
                    }
                }
            }

            companion object {
                fun from(orderSheet: OrderSheet): PurchaseOrder {
                    return orderSheet.run {
                        PurchaseOrder(
                            id.toString(),
                            createdAt,
                            userId,
                            productCode,
                            topicId,
                            products.map { OrderedProduct.from(it) },
                            adjustmentId,
                            status,
                            getPrivacy(),
                            modifiedAt,
                            preDefineField,
                            policyCapture?.snapShotPolicyCards
                        )
                    }
                }
            }
        }
    }
}
