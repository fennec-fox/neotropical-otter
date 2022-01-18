package io.mustelidae.otter.neotropical.api.domain.booking

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    indexes = [
        Index(name = "IDX_FK_ITEM_BOOKINGID", columnList = "booking_id,id", unique = true)
    ]
)
class Item(
    @Column(length = 200)
    val name: String,
    val price: Long? = null,
    val discount: Long? = null,
    var description: String? = null,
    @Column(length = 32)
    val verticalId: String? = null
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set
}
