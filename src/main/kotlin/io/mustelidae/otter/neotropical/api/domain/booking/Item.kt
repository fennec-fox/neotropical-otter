package io.mustelidae.otter.neotropical.api.domain.booking

import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
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
        Index(name = "IDX_FK_ITEM_BOOKING-ID", columnList = "booking_id,id", unique = true)
    ]
)
class Item(
    @Column(length = 200)
    val name: String,
    var description: String? = null,
    val price: Long? = null,
    val discount: Long? = null,
    @Column(length = 32)
    val verticalId: Long? = null
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var canceledDate: LocalDateTime? = null
        protected set
    var refundPrice: Long? = null
        protected set
    var cancelFee: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(length = 15, nullable = false)
    var status = Status.ORDERED
        protected set

    @Column(length = 500)
    var note: String? = null

    enum class Status {
        ORDERED,
        CANCELED,
        COMPLETED
    }

    @ManyToOne
    @JoinColumn(name = "booking_id")
    var booking: Booking? = null
        protected set

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "item", fetch = FetchType.EAGER)
    var itemOptions: MutableList<ItemOption> = arrayListOf()
        protected set

    fun complete() {
        this.status = Status.COMPLETED
        this.itemOptions.filter { it.status == ItemOption.Status.ORDERED }
            .forEach { it.status = ItemOption.Status.COMPLETED }
    }

    fun cancel() {
        this.status = Status.CANCELED
        this.itemOptions.filter { it.status == ItemOption.Status.ORDERED }
            .forEach { it.status = ItemOption.Status.CANCELED }
    }

    fun setBy(booking: Booking) {
        this.booking = booking
        if (booking.items.contains(this).not())
            booking.addBy(this)
    }

    fun addBy(itemOption: ItemOption) {
        itemOptions.add(itemOption)
        if (itemOption.item != this)
            itemOption.setBy(this)
    }

    fun getTotalPrice(): Long {
        val price = ((price ?: 0) - (discount ?: 0))
        val priceOfOption = itemOptions.sumOf { it.price ?: 0 }
        return price + priceOfOption
    }
}
