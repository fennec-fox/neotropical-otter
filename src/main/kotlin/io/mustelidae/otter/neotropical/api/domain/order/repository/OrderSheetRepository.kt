package io.mustelidae.otter.neotropical.api.domain.order.repository

import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderSheetRepository : MongoRepository<OrderSheet, ObjectId>
