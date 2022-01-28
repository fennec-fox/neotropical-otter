package io.mustelidae.otter.neotropical.api.domain.checkout

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.checkout.api.CheckoutResources
import io.mustelidae.otter.neotropical.api.domain.order.OrderForm
import io.mustelidae.otter.neotropical.api.domain.order.repository.OrderSheetRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class CheckoutInteraction(
    private val orderSheetRepository: OrderSheetRepository
) {

    fun checkout(
        productCode: ProductCode,
        topicId: String,
        request: CheckoutResources.Request.Checkout
    ): ObjectId {
        val orderForm = OrderForm(productCode, topicId).apply {
            createSheet(request)
        }

        if (orderForm.inspect().not())
            orderForm.getErrors().let {
                throw DevelopMistakeException(it.toString())
            }

        val orderSheet = orderForm.getSheet()

        orderSheetRepository.save(orderSheet)
        return orderSheet.id
    }
}
