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

    private fun make(
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
            product.reservationDate,
            product.price
        ).apply {
            setContent(product.contents)
            product.location?.let {
                setLocation(it)
            }
        }
        product.goods?.flatMap { make(it) }?.forEach { booking.addBy(it) }
        return booking
    }

    private fun make(goods: OrderSheet.Product.Goods): List<Item> {
        val items = mutableListOf<Item>()
        for (i in 1..goods.quantity) {
            goods.run {
                val item = Item(name, description, priceOfUnit, discountPriceOfUnit, id)
                goods.goodsOptions?.map { make(it) }?.forEach { item.addBy(it) }
                items.add(item)
            }
        }

        return items
    }

    private fun make(goodsOption: OrderSheet.Product.Goods.GoodsOption): ItemOption {
        return goodsOption.run {
            ItemOption(name, description, price)
        }
    }
}
