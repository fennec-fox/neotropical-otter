package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.booking.BookingInteraction
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Booking")
@RestController
@RequestMapping("/v1/bookings")
class BookingController(
    private val bookingFinder: BookingFinder,
    private val bookingInteraction: BookingInteraction
) {

    @Operation(summary = "Confirm Book", description = "When the reservation is confirmed, the status of the booking changes to 'BOOKED'.")
    @PutMapping("{bookingIds}")
    fun confirm(
        @PathVariable bookingIds: List<Long>
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XSystem).validOrThrow(bookings)
        bookingInteraction.confirm(bookingIds)
        return Unit.toReply()
    }
}
