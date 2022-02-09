package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.common.design.v1.component.DisplayCard
import io.mustelidae.otter.neotropical.api.common.design.v1.component.ImageCard
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.Label
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.CallOffBooking
import io.mustelidae.otter.neotropical.api.domain.vertical.CancellationUnit
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.RecordCard
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord
import java.util.*
import kotlin.random.Random

class StableMockUpClient : VerticalClient {
    override fun cancel(
        userId: Long,
        bookingIds: List<Long>,
        cause: String
    ): ExchangeResult = ExchangeResult(true)

    override fun cancelByItem(cancellationUnit: CancellationUnit, cause: String): ExchangeResult = ExchangeResult(true)

    override fun findRecord(bookingId: Long): VerticalRecord {
        return VerticalRecord(
            Random.nextLong(),
            bookingId,
            Label("Test Status"),
            RecordCard(
                listOf(
                    DisplayCard(
                        1, "First", "Content",
                        listOf(
                            DisplayCard.Content(
                                "First Content", DisplayCard.Content.Type.LINK,
                                DisplayCard.Content.ContentBox(
                                    link = DisplayCard.Content.ContentBox.Link(
                                        "It is link",
                                        "https://naver.com"
                                    )
                                )
                            ),
                            DisplayCard.Content("Second Content", DisplayCard.Content.Type.TEXT, DisplayCard.Content.ContentBox(text = "This is Context")),
                        )
                    ),
                ),
                listOf(
                    ImageCard(
                        1, "Logo!",
                        listOf(
                            ImageCard.Image(1, "https://sk.com/lib/images/desktop/logo.png", "https://sk.com/lib/images/desktop/logo.png", "SK 로고"),
                            ImageCard.Image(2, "http://www.skenergy.com/images/common/logo_main.png", "SK 에너지 로고")
                        )
                    ),
                    ImageCard(
                        2, null,
                        listOf(
                            ImageCard.Image(1, "https://sk.com/lib/images/desktop/logo.png", null)
                        )
                    ),
                )
            ),
            mapOf(
                Pair("testA", "It's good day to die!"),
                Pair("testList", listOf("hello", "world!"))
            )
        )
    }

    override fun obtain(bookings: List<Booking>, orderSheet: OrderSheet): ExchangeResult = ExchangeResult(true)

    override fun askWhetherCallOff(userId: Long, bookingId: Long): CallOffBooking = CallOffBooking(true, 0, 0)
}
