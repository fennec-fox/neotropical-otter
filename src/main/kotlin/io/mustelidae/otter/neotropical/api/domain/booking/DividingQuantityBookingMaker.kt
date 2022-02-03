package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet

class DividingQuantityBookingMaker(
    private val orderSheet: OrderSheet
) : BookingMaker {

    override fun make(): List<Booking> {
        return orderSheet.products.map {
            make(orderSheet.userId, orderSheet.productCode, orderSheet.topicId, orderSheet.id.toString(), it)
        }
    }

    fun make(
        userId: Long,
        productCode: ProductCode,
        topicId: String,
        orderId: String,
        product: OrderSheet.Product
    ): Booking {
        val booking = Booking(
            userId,
            productCode,
            topicId,
            orderId,
            product.title,
            product.description,
            product.reservationDate
        )
        TODO()
    }

    fun make(goods: OrderSheet.Product.Goods): List<Item> {
        TODO()
    }

    private fun make(goodsOption: OrderSheet.Product.Goods.GoodsOption): ItemOption {
        return goodsOption.run {
            ItemOption(name, description, price)
        }
    }
}
