package io.mustelidae.otter.neotropical.api.domain.checkout

import io.mustelidae.otter.neotropical.api.common.Privacy
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import java.time.LocalDateTime

class CheckoutResources {

    class Request {

        class Checkout(
            val products: List<OrderSheet.Product>,
            var preDefineField: Map<String, Any?>? = null,
            val privacy: Privacy? = null
        ) {
            data class ProductOrder(
                val title: String,
                val price: Long? = null,
                val description: String? = null,
                val reservationDate: LocalDateTime? = null,
                val contents: List<SimpleContent>,
                val id: Long? = null,
                val preDefineField: Map<String, Any?>? = null,
            ) {

                data class GoodsOrder(
                    val name: String,
                    val quantity: Int,
                    val id: Long? = null,
                    val priceOfUnit: Long? = null,
                    val discountPriceOfUnit: Long? = null,
                    val description: String? = null,
                    val goodsOptions: List<GoodsOptionOrder>?
                ) {

                    data class GoodsOptionOrder(
                        val name: String,
                        var description: String? = null,
                        val price: Long? = null,
                        val id: Long? = null,
                    )
                }
            }
        }
    }
}
