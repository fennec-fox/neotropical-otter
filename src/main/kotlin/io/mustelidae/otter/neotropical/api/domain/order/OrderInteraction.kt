package io.mustelidae.otter.neotropical.api.domain.order

import io.mustelidae.otter.neotropical.api.domain.order.repository.OrderSheetRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class OrderInteraction(
    private val orderSheetFinder: OrderSheetFinder,
    private val orderSheetRepository: OrderSheetRepository
) {
    fun complete(orderId: ObjectId) {
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        orderSheet.ordered()
        orderSheetRepository.save(orderSheet)
    }

    fun fail(orderId: ObjectId) {
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        orderSheet.failed()
        orderSheetRepository.save(orderSheet)
    }
}
