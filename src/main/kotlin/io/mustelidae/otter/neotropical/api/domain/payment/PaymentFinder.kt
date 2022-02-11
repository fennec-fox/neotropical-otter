package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.config.DataNotFindException
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.repository.PaymentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PaymentFinder(
    private val paymentRepository: PaymentRepository,
    private val billingPayClient: BillingPayClient
) {

    fun findOneWithPaidReceipt(id: Long): PaidReceipt? {
        val payment = paymentRepository.findByIdOrNull(id) ?: throw DataNotFindException(id, "payment not found")
        return if (payment.billPayId != null) {
            billingPayClient.findByReceipt(payment.bookings.first().productCode, payment.userId, payment.billPayId!!)
        } else
            null
    }
}
