package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.SearchCondition
import io.mustelidae.otter.neotropical.api.config.DataNotFindException
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingDSLRepository
import io.mustelidae.otter.neotropical.api.domain.booking.repsitory.BookingRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BookingFinder
@Autowired constructor(
    private val bookingDSLRepository: BookingDSLRepository,
    private val bookingRepository: BookingRepository
) {

    fun findInWithPayment(bookingIds: List<Long>): List<Booking> {
        val bookings = bookingDSLRepository.findAllByIdWithPayment(bookingIds) ?: emptyList()
        if (bookings.size != bookingIds.size)
            throw DataNotFindException("Couldn't find all the booking for the requested ID.")

        return bookings
    }

    fun findAllByIds(bookingIds: List<Long>): List<Booking> {
        val bookings = bookingRepository.findAllById(bookingIds)
        if (bookings.size != bookingIds.size)
            throw DataNotFindException("Couldn't find all the booking for the requested ID.")

        return bookings
    }

    fun findAllActive(userId: Long): List<Booking> {
        return bookingDSLRepository.findAllWithStatus(userId, Booking.Status.WAIT, Booking.Status.BOOKED) ?: emptyList()
    }

    fun findAllRecord(
        userId: Long,
        limit: Int,
        lastBookingId: Long? = null
    ): List<Booking> {
        return bookingDSLRepository.findAllRecordWithStatus(
            userId,
            limit,
            lastBookingId,
            Booking.Status.CANCELED,
            Booking.Status.COMPLETED
        )
    }

    fun findOneWithItem(
        bookingId: Long
    ): Booking {
        return bookingDSLRepository.findOne(bookingId) ?: throw DataNotFindException(bookingId, "Booking does not exist.")
    }

    fun findAllByOrderId(orderId: ObjectId): List<Booking> {
        return bookingRepository.findAllByOrderId(orderId.toString())
    }

    fun findRecentUsage(
        userId: Long,
        productCode: ProductCode,
        topicId: String? = null,
    ): List<Booking> {
        val limit = 3
        return bookingDSLRepository.findRecentAllWithStatus(userId, limit, productCode, topicId, Booking.Status.COMPLETED)
    }

    fun search(searchCondition: SearchCondition<Booking>): List<Booking> {
        return bookingRepository.findAll(searchCondition.toSpecs())
    }
}
