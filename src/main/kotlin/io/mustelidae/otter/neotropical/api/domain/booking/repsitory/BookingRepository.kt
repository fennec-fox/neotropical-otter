package io.mustelidae.otter.neotropical.api.domain.booking.repsitory

import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking>
