package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.v1

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.common.design.v1.component.Label
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.ApiModelType
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingPage
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import java.time.LocalDateTime

class GWActiveBookingResources {

    class Reply {

        data class ActiveBooking(
            val id: Long,
            val productCode: ProductCode,
            val topicId: String,
            val createAt: LocalDateTime,
            val modifiedAt: LocalDateTime,
            val type: ApiModelType,
            val title: String,
            val status: Label,
            val landingType: LandingWay,
            val isDisable: Boolean = false,
            val landingPath: String,
            val contents: List<SimpleContent>,
        ) {
            companion object {
                fun from(booking: Booking, landingPage: LandingPage): ActiveBooking {
                    val contents = booking.getContent() ?: listOf(
                        SimpleContent(Label("title"), listOf(booking.title))
                    )
                    return booking.run {
                        ActiveBooking(
                            id!!,
                            productCode,
                            topicId,
                            createdAt!!,
                            modifiedAt!!,
                            ApiModelType.BOOKING,
                            title,
                            Label(
                                status.text,
                                color = Label.Color.BLUE
                            ),
                            landingPage.getLandingWay(),
                            false,
                            landingPage.getActiveDetail()!!,
                            contents
                        )
                    }
                }
            }
        }
    }
}
