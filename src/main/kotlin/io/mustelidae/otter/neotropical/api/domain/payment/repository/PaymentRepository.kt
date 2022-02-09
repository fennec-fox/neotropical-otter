package io.mustelidae.otter.neotropical.api.domain.payment.repository

import io.mustelidae.otter.neotropical.api.domain.payment.Payment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment>
