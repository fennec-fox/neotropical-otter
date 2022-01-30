package io.mustelidae.otter.neotropical.api.domain.order

import io.mustelidae.otter.neotropical.api.config.DataNotFindException
import io.mustelidae.otter.neotropical.api.domain.order.repository.OrderSheetRepository
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrderSheetFinder(
    private val orderSheetRepository: OrderSheetRepository
) {

    fun findOne(id: ObjectId): OrderSheet? {
        return orderSheetRepository.findByIdOrNull(id)
    }

    fun findOneOrThrow(id: ObjectId): OrderSheet {
        return findOne(id) ?: throw DataNotFindException("Order information does not exist.")
    }
}
