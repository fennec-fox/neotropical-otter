package io.mustelidae.otter.neotropical.api.domain.order

import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import org.springframework.validation.Errors
import java.time.LocalDateTime

class OrderForm(
    private val productCode: ProductCode,
    private val topicId: String,
) {
    constructor(orderSheet: OrderSheet) : this(orderSheet.productCode, orderSheet.topicId) {
        this.orderSheet = orderSheet
    }

    lateinit var orderSheet: OrderSheet
    private var errors: Errors = OrderFormSpecificationErrors()

    fun createSheet(request: CheckoutResources.Request.Checkout) {
        request.run {
            orderSheet = OrderSheet(
                userId,
                productCode,
                topicId,
                products.map {
                    OrderSheet.Product(
                        it.title,
                        it.contents,
                        it.goodsOrders?.map { order -> createGoods(order) },
                        it.id,
                        it.price,
                        it.description,
                        it.reservationDate,
                        it.preDefineField
                    )
                },
                adjustmentId,
                preDefineField,
                settlementDate
            )

            privacy?.let { orderSheet.setPrivacy(it) }
        }
    }

    private fun createGoods(request: CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder): OrderSheet.Product.Goods {
        request.run {
            val options = goodsOptionOrders?.map { order ->
                OrderSheet.Product.Goods.GoodsOption(
                    order.name, order.description, order.price, order.id
                )
            }

            return OrderSheet.Product.Goods(
                name,
                quantity,
                id,
                priceOfUnit,
                discountPriceOfUnit,
                description,
                options
            )
        }
    }

    fun inspect(): Boolean {
        inspectPrice()
        inspectSettlementDate()

        return errors.hasErrors().not()
    }

    private fun inspectSettlementDate() {
        if (orderSheet.settlementDate == null) {
            val existDate = orderSheet.products.filter { it.reservationDate != null }
            if (existDate.isNullOrEmpty())
                errors.rejectValue(
                    "parameter",
                    ErrorCode.HI01.toString(),
                    arrayOf("accountSettlementDate", "reservationDate"),
                    "One of the reservation date and expected settlement date information must be entered."
                )
        }
    }

    private fun inspectPrice() {
        var sumOrNull: Long? = null

        for (product in orderSheet.products) {
            product.price?.let { sumOrNull = + it }

            if (product.goods != null) {
                for (goods in product.goods) {
                    goods.priceOfUnit?.let { sumOrNull = + it }

                    if (goods.goodsOptions != null) {
                        for (goodsOption in goods.goodsOptions)
                            goodsOption.price?.let { sumOrNull = + it }
                    }
                }
            }
        }

        if (sumOrNull == null)
            errors.rejectValue(
                "parameter",
                ErrorCode.HI01.toString(),
                arrayOf("price", "priceOfUnit"),
                "The sum of the product amounts cannot be null."
            )
    }

    fun stompingPolicy(policyCards: List<PolicyCard>) {
        if (orderSheet.policyCapture != null)
            throw IllegalStateException("The policy has already been written.")
        orderSheet.writeSnapShot(OrderSheet.PolicyCapture(LocalDateTime.now(), policyCards))
    }

    fun getSheet(): OrderSheet = orderSheet
    fun getErrors(): Errors = errors
}
