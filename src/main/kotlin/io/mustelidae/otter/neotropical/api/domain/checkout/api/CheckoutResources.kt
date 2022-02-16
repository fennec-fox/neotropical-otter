package io.mustelidae.otter.neotropical.api.domain.checkout.api

import io.mustelidae.otter.neotropical.api.common.Location
import io.mustelidae.otter.neotropical.api.common.Privacy
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

class CheckoutResources {

    class Request {

        @Schema(name = "Checkout.Request.Checkout")
        class Checkout(
            val userId: Long,
            val products: List<ProductOrder>,
            val adjustmentId: Long,
            var preDefineField: Map<String, Any>? = null,
            val privacy: Privacy? = null,
            val settlementDate: LocalDate? = null
        ) {
            @Schema(name = "Checkout.Request.Checkout.ProductOrder")
            data class ProductOrder(
                val title: String,
                val contents: List<SimpleContent>,
                val goodsOrders: List<GoodsOrder>? = null,
                val price: Long? = null,
                val description: String? = null,
                val reservationDate: LocalDateTime? = null,
                val id: Long? = null,
                val preDefineField: Map<String, Any?>? = null,
                val location: Location? = null
            ) {

                @Schema(name = "Checkout.Request.Checkout.ProductOrder.GoodsOrder")
                data class GoodsOrder(
                    val name: String,
                    val quantity: Int,
                    val id: Long? = null,
                    val priceOfUnit: Long? = null,
                    val discountPriceOfUnit: Long? = null,
                    val description: String? = null,
                    val goodsOptionOrders: List<GoodsOptionOrder>? = null
                ) {

                    @Schema(name = "Checkout.Request.Checkout.ProductOrder.GoodsOrder.GoodsOptionOrder")
                    data class GoodsOptionOrder(
                        val name: String,
                        var description: String? = null,
                        val price: Long? = null,
                        val id: Long? = null
                    )
                }
            }

            companion object
        }
    }
}
