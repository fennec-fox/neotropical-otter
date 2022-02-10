package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord
import org.springframework.stereotype.Service

@Service
class VerticalFinder(
    val verticalHandler: VerticalHandler
) {

    fun findRecord(productCode: ProductCode, bookingId: Long): VerticalRecord {
        return verticalHandler.getClient(productCode).findRecord(bookingId)
    }
}
