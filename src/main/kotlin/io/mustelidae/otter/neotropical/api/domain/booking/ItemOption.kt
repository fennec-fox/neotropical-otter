package io.mustelidae.otter.neotropical.api.domain.booking

import io.mustelidae.otter.neotropical.api.common.Audit
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "IDX_FK_ITEM-OPTION_ITEM-ID", columnList = "item_id,id", unique = true)
    ]
)
class ItemOption(
    @Column(length = 200)
    val name: String,
    var description: String? = null,
    val price: Long? = null,
) : Audit() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "item_id")
    var item: Item? = null
        protected set

    var status: Status = Status.ORDERED

    enum class Status {
        ORDERED,
        CANCELED,
        COMPLETED
    }

    fun setBy(item: Item) {
        this.item = item
        if (item.itemOptions.contains(this).not())
            item.addBy(this)
    }
}
