package io.mustelidae.otter.neotropical.api.domain.booking.repsitory

import com.querydsl.core.types.dsl.BooleanExpression
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.booking.QBooking.booking
import io.mustelidae.otter.neotropical.api.domain.booking.QItem.item
import io.mustelidae.otter.neotropical.api.domain.payment.QPayment.payment
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class BookingDSLRepository : QuerydslRepositorySupport(Booking::class.java) {

    fun findAll(userId: Long, start: LocalDateTime, end: LocalDateTime, productCode: ProductCode? = null): List<Booking>? {
        return from(booking)
            .innerJoin(booking.items, item).fetchJoin()
            .innerJoin(booking.payment, payment).fetchJoin()
            .where(
                booking.userId.eq(userId).and(booking.createdAt.between(start, end)),
                eqProductCode(productCode),
                booking.isHide.isFalse
            )
            .distinct()
            .fetch()
    }

    fun findAllByIdWithPayment(bookingIds: List<Long>): List<Booking>? {
        return from(booking)
            .innerJoin(booking.payment, payment).fetchJoin()
            .where(
                booking.id.`in`(bookingIds)
            ).fetch()
    }

    fun findAllWithStatus(userId: Long, vararg statusList: Booking.Status): List<Booking>? {
        return from(booking)
            .innerJoin(booking.payment, payment).fetchJoin()
            .where(
                booking.userId.eq(userId),
                booking.status.`in`(*statusList),
                booking.isHide.isFalse
            ).fetch().distinct()
    }

    fun findRecentAllWithStatus(
        userId: Long,
        limit: Int,
        productCode: ProductCode,
        topicId: String? = null,
        vararg statusList: Booking.Status
    ): List<Booking> {
        val query = from(booking)
            .innerJoin(booking.payment, payment).fetchJoin()
            .where(
                booking.userId.eq(userId),
                booking.status.`in`(*statusList),
                booking.productCode.eq(productCode),
                topicId?.let { booking.topicId.lt(it) },
                booking.isHide.isFalse
            ).orderBy(booking.createdAt.desc())

        return query.limit(limit.toLong()).fetch() ?: emptyList()
    }

    fun findAllRecordWithStatus(
        userId: Long,
        limit: Int,
        lastBookingId: Long? = null,
        vararg statusList: Booking.Status
    ): List<Booking> {
        val start = LocalDate.now().minusYears(1).atStartOfDay()
        val query = from(booking)
            .innerJoin(booking.payment, payment).fetchJoin()
            .where(
                booking.userId.eq(userId),
                booking.status.`in`(*statusList),
                lastBookingId?.let { booking.id.lt(it) },
                booking.createdAt.goe(start),
                booking.isHide.isFalse
            ).orderBy(booking.createdAt.desc())

        return query.limit(limit.toLong()).fetch() ?: emptyList()
    }

    fun findOne(id: Long): Booking? {
        return from(booking)
            .innerJoin(booking.items, item).fetchJoin()
            .innerJoin(booking.payment, payment).fetchJoin()
            .where(
                booking.id.eq(id)
            ).fetchOne()
    }

    private fun eqProductCode(productCode: ProductCode?): BooleanExpression? {
        return if (productCode != null) {
            booking.productCode.eq(productCode)
        } else null
    }
}
