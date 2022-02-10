package io.mustelidae.otter.neotropical.api.domain.payment.voucher.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import java.time.LocalDateTime

class VoucherClientResources {

    class Reply {

        data class Detail(
            val id: Long,
            val productCode: ProductCode,
            val topicId: String,
            val name: String,
            val description: String,
            val startDate: LocalDateTime? = null,
            val endDate: LocalDateTime? = null,
            val groupId: Long? = null
        )
    }
}
