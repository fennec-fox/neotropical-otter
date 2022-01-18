package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.ApiModelType
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.Label
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import java.time.LocalDateTime

class GWActiveBookingResources {

    class Reply {

        data class ActiveBooking(
            val id: Long,
            val productCode: ProductCode,
            val topicId: String,
            val createAt: LocalDateTime,
            val updateAt: LocalDateTime,
            val type: ApiModelType,
            val title: String,
            val status: Label,

            val landingType: LandingWay,
            val isDisable: Boolean = false,
            val landingPath: String? = null,

            val contents: List<SimpleContent> = emptyList(),
        )
    }
}
