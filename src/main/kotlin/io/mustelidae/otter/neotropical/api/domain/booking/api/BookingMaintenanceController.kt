package io.mustelidae.otter.neotropical.api.domain.booking.api

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Replies
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.SearchCondition
import io.mustelidae.otter.neotropical.api.common.toReplies
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.config.HumanException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.BookingFinder
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.mustelidae.otter.neotropical.utils.and
import io.mustelidae.otter.neotropical.utils.between
import io.mustelidae.otter.neotropical.utils.equal
import io.mustelidae.otter.neotropical.utils.`in`
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.jpa.domain.Specification
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalTime

@Tag(name = "Booking")
@RestController
@RequestMapping("/v1/maintenance/bookings")
class BookingMaintenanceController(
    private val bookingFinder: BookingFinder
) {

    @Parameter(name = RoleHeader.XAdmin.KEY, description = RoleHeader.XAdmin.NAME)
    @GetMapping("{bookingId}")
    fun findOne(
        @PathVariable bookingId: Long
    ): Reply<BookingResources.Reply.BookingOfOrderedWithItems> {
        val booking = bookingFinder.findOneWithItem(bookingId)
        DataAuthentication(RoleHeader.XAdmin).validOrThrow(booking)
        return BookingResources.Reply.BookingOfOrderedWithItems.from(booking)
            .toReply()
    }

    @Parameter(name = RoleHeader.XAdmin.KEY, description = RoleHeader.XAdmin.NAME)
    @GetMapping
    fun search(
        @RequestParam productCode: ProductCode,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate? = null,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate? = null,
        @RequestParam userId: Long? = null,
        @RequestParam topicId: String? = null,
        @RequestParam bookingIds: List<Long>? = null,
        @RequestParam verticalBookingId: String? = null,
        @RequestParam status: Booking.Status? = null
    ): Replies<BookingResources.Reply.BookingOfOrdered> {
        val condition = BookingSearchCondition(
            productCode,
            topicId,
            startDate,
            endDate,
            userId,
            bookingIds,
            verticalBookingId,
            status
        )

        val bookings = bookingFinder.search(condition)
        DataAuthentication(RoleHeader.XAdmin).validOrThrow(bookings)
        return bookings
            .map { BookingResources.Reply.BookingOfOrdered.from(it) }
            .toReplies()
    }
}

internal class BookingSearchCondition(
    private val productCode: ProductCode,
    private val topicId: String?,
    private val start: LocalDate?,
    private val end: LocalDate?,
    private val userId: Long?,
    private val bookingIds: List<Long>?,
    private val verticalBookingId: String?,
    private val status: Booking.Status?
) : SearchCondition<Booking> {
    override fun isValid(): Boolean {
        return super.isValidDate(start, end)
    }

    override fun toSpecs(): Specification<Booking> {
        if (this.isValid().not())
            throw HumanException(Error(ErrorCode.HI01, "Invalid search input."))

        return and(
            Booking::productCode.equal(productCode),
            topicId?.let { Booking::topicId.equal(it) },
            start?.let { Booking::createdAt.between(start.atStartOfDay(), end!!.atTime(LocalTime.MAX)) },
            userId?.let { Booking::userId.equal(it) },
            bookingIds?.let { Booking::id.`in`(it) },
            verticalBookingId?.let { Booking::verticalId.equal(it) },
            status?.let { Booking::status.equal(it) }
        )
    }
}
