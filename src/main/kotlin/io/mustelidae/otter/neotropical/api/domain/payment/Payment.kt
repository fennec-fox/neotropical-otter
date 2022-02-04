package io.mustelidae.otter.neotropical.api.domain.payment

import io.mustelidae.otter.neotropical.api.common.Audit
import io.mustelidae.otter.neotropical.api.common.method.pay.PaymentMethod
import io.mustelidae.otter.neotropical.api.common.method.pay.Voucher
import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import org.bson.types.ObjectId
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.EnumType.*
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "IDX_PAYMENT_USER", columnList = "userId,createdAt"),
        Index(name = "IDX_PAYMENT-ID", columnList = "paymentId")
    ]
)
class Payment(
    val userId: Long,
    val priceOfOrder: Long,
    @Enumerated(STRING)
    val payType: PayWay.Type
) : Audit() {

    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var adjustmentId: Long? = null

    @Column(length = 40)
    private var paymentOrderId: String? = null

    private var textOfMethods: String? = null

    @Enumerated(STRING)
    @Column(length = 20, nullable = false)
    var status: Status = Status.PENDING
        protected set

    // voucher
    var voucherId: Long? = null

    // pay
    var paymentId: Long? = null
    @Column(length = 100)
    var payKey: String? = null
    var paidAmount: Long? = null
        protected set
    var paidDate: LocalDateTime? = null
        protected set

    // cancel (refund)
    var refundAmount: Long? = null
    var penaltyAmount: Long? = null
    var cancelledDate: LocalDateTime? = null

    @Column(length = 3000)
    var note: String? = null
        protected set

    fun appendMemo(memo: String) {
        this.note = if (this.note == null)
            memo
        else
            this.note.plus(" | ").plus(memo)
    }

    @OneToMany(mappedBy = "payment")
    var bookings: MutableList<Booking> = arrayListOf()
        protected set

    enum class Status {
        PENDING,
        PAY,
        CANCEL_ALL,
        CANCEL_PARTIAL
    }

    fun getMethods(): List<PaymentMethod> {
        return textOfMethods!!.split(",")
            .map { PaymentMethod.valueOf(it) }
    }

    fun addBy(booking: Booking) {
        bookings.add(booking)
        if (booking.payment != this)
            booking.setBy(this)
    }

    fun usingVoucher(voucher: Voucher) {
        this.voucherId = voucher.id
        this.textOfMethods = voucher.paymentMethod.name
        this.paidDate = LocalDateTime.now()
    }

    fun pay(
        amountOfPay: Long,
        paymentOrderId: ObjectId,
        payKey: String?,
        adjustmentId: Long? = null
    ) {
        if (status != Status.PENDING)
            throw DevelopMistakeException("Payment is not pending.")

        this.status = Status.PAY
        this.paidDate = LocalDateTime.now()
        this.adjustmentId = adjustmentId
        this.paymentOrderId = paymentOrderId.toString()
        this.payKey = payKey

        if (payKey.isNullOrBlank().not())
            this.textOfMethods = PaymentMethod.CARD.name
    }

    fun repay(
        amountOfRepay: Long,
        adjustmentId: Long? = null
    ) {
        if (status != Status.PAY)
            throw DevelopMistakeException("Repayment is possible only after payment has been completed.")

        this.paidAmount = paidAmount
        this.paidDate = LocalDateTime.now()
        this.adjustmentId = adjustmentId
    }

    fun paid(
        paymentId: Long,
        paidAmount: Long,
        paidMethods: List<PaymentMethod>,
        pgPaidDate: LocalDateTime
    ) {
        this.paymentId = paymentId
        this.paidAmount = paidAmount
        this.textOfMethods = paidMethods.joinToString()
        this.paidDate = pgPaidDate
    }

    fun isFreePayment(): Boolean = this.paidAmount == 0L

    fun isPaid(): Boolean {
        if (this.status == Status.PENDING &&
            this.paymentId == null
        )
            return false
        return true
    }

    fun getFinallyPaymentAmount(): Long {
        return (paidAmount ?: 0) + (refundAmount ?: 0)
    }

    fun cancelEntire(cancelDate: LocalDateTime, amountOfPenalty: Long) {
        status = Status.CANCEL_ALL
        this.cancelledDate = cancelDate
        refundAmount = -1 * paidAmount!! + amountOfPenalty
        this.penaltyAmount = amountOfPenalty
    }

    fun cancelByChangeOnlyStatus() {
        status = Status.CANCEL_ALL
        cancelledDate = LocalDateTime.now()
    }

    fun cancelPartial(cancelDate: LocalDateTime, amountOfPartialCancel: Long, amountOfPenalty: Long,) {
        status = Status.CANCEL_PARTIAL
        cancelledDate = cancelDate
        refundAmount = (refundAmount ?: 0) + (-1 * amountOfPartialCancel + amountOfPenalty)
        penaltyAmount = (penaltyAmount ?: 0) + amountOfPenalty
    }
}
