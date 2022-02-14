package io.mustelidae.otter.neotropical.api.domain.cancel.api

import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.domain.cancel.BookingCancelInteraction
import io.mustelidae.otter.neotropical.api.lock.EnableUserLock
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Cancel")
@RestController
@RequestMapping("/v1/maintenance/bookings")
class BookingCancelMaintenanceController(
    private val bookingFinder: BookingFinder,
    private val bookingCancelInteraction: BookingCancelInteraction
) {

    @Parameter(name = RoleHeader.XAdmin.KEY, description = RoleHeader.XAdmin.NAME)
    @EnableUserLock
    @DeleteMapping("{bookingIds}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancel(
        @PathVariable bookingIds: List<Long>,
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @RequestParam cancelFee: Long,
        @RequestParam cause: String
    ): Reply<Unit> {
        val bookings = bookingFinder.findAllByIds(bookingIds)
        DataAuthentication(RoleHeader.XAdmin, RoleHeader.XUser).validOrThrow(bookings)
        bookingCancelInteraction.cancelWithOutCallOff(bookingIds, cancelFee, cause)
        return Unit.toReply()
    }
}
