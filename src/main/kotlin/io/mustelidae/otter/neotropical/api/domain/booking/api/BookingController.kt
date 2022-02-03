package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.lock.EnableUserLock
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/bookings")
class BookingController(
    private val orderSheetFinder: OrderSheetFinder
) {

    @PostMapping("pre-pay-book")
    @EnableUserLock
    fun book(
        @RequestHeader userId: Long,
        @RequestBody request: BookingResources.Request.PrePayBook
    ): Replies<Long> {
        val orderId = ObjectId(request.orderId)
        val orderSheet = orderSheetFinder.findOneOrThrow(orderId)
        DataAuthentication(RoleHeader.XUser).validOrThrow(orderSheet)
        TODO()
    }
}
