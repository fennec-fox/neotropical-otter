package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.common.Audit
import io.mustelidae.otter.neotropical.api.common.Location
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.payment.Payment
import io.mustelidae.otter.neotropical.utils.fromJson
import io.mustelidae.otter.neotropical.utils.toJson
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "IDX_BOOKING_USER", columnList = "userId,createdAt"),
        Index(name = "IDX_BOOKING_ORDER-ID", columnList = "orderId"),
        Index(name = "IDX_BOOKING_CREATED-AT", columnList = "createdAt"),
        Index(name = "IDX_FK_BOOKING_PAYMENT-ID", columnList = "payment_id,id", unique = true)
    ]
)
class Booking(
    val userId: Long,
    val productCode: ProductCode,
    val topicId: String,
    val orderId: String,
    val title: String,
    val description: String? = null,
    val reservationDate: LocalDateTime ? = null,
    val price: Long? = null
) : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var verticalId: Long? = null

    // https://hibernate.atlassian.net/browse/HHH-14935
    @Suppress("DEPRECATION")
    @Type(type = "text")
    var textOfContent: String? = null
        protected set
    @Column(length = 3000)
    var textOfLocation: String? = null
        protected set

    var isHide = false

    var status: Status = Status.WAIT
        protected set

    @ManyToOne(cascade = [ALL], fetch = LAZY)
    @JoinColumn(name = "payment_id")
    var payment: Payment? = null
        protected set

    @OneToMany(cascade = [ALL], mappedBy = "booking")
    var items: MutableList<Item> = arrayListOf()
        protected set

    fun setBy(payment: Payment) {
        this.payment = payment
        if (payment.bookings.contains(this).not())
            payment.addBy(this)
    }

    fun setContent(contents: List<SimpleContent>) {
        textOfContent = contents.toJson()
    }

    fun getContent(): List<SimpleContent>? = textOfContent?.fromJson()

    fun setLocation(location: Location) {
        textOfLocation = location.toJson()
    }

    fun getLocation(): Location? = textOfLocation?.fromJson()

    fun book() {
        if (status != Status.WAIT)
            throw DevelopMistakeException("Booking [$id] is not in 'WAIT'.")
        this.status = Status.BOOKED
    }

    fun cancel() {
        if (status == Status.CANCELED)
            throw DevelopMistakeException("This booking has already been canceled.")

        this.items
            .filter { it.status != Item.Status.CANCELED }
            .forEach { it.cancel() }
        this.status = Status.CANCELED
    }

    fun completed() {
        if (status != Status.BOOKED)
            throw DevelopMistakeException("Booking [$id] is not in 'BOOKED'.")

        this.items
            .filter { it.status == Item.Status.ORDERED }
            .forEach { it.complete() }
        this.status = Status.COMPLETED
    }

    enum class Status(val text: String) {
        WAIT("Wait booking"),
        BOOKED("booked"),
        COMPLETED("completed"),
        CANCELED("booking canceled")
    }

    fun addBy(item: Item) {
        items.add(item)
        if (item.booking != this)
            item.setBy(this)
    }

    fun getTotalPrice(): Long {
        return this.items.sumOf { it.getTotalPrice() } + (price ?: 0)
    }

    companion object
}
