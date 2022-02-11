package io.mustelidae.otter.neotropical.api.domain.order

import io.mustelidae.otter.neotropical.api.common.Privacy
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.common.design.v1.component.PolicyCard
import io.mustelidae.otter.neotropical.api.config.CheckoutTimeoutException
import io.mustelidae.otter.neotropical.api.domain.payment.method.UsingPayMethod
import io.mustelidae.otter.neotropical.utils.Crypto
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Document
class OrderSheet(
    val userId: Long,
    val productCode: ProductCode,
    val topicId: String,
    val products: List<Product>,
    val adjustmentId: Long,
    var preDefineField: Map<String, Any>?,
    val settlementDate: LocalDate? = null
) : Sheet {
    override var schemaVersion: Long = 1

    @Id
    var id: ObjectId = ObjectId()
        private set
    var createdAt = LocalDateTime.now()!!
        private set

    var modifiedAt: LocalDateTime? = null
        private set

    var status: Status = Status.WAIT
        private set

    enum class Status {
        /* 주문 대기 */
        WAIT,
        /* 주문 완료 */
        ORDERED,
        /* 주문 실패 */
        FAIL
    }

    var policyCapture: PolicyCapture? = null
        private set

    var textOfPrivacy: String? = null
        private set

    var estimateUsingPayMethod: UsingPayMethod? = null
        private set

    fun setPrivacy(
        privacy: Privacy
    ) {
        textOfPrivacy = Crypto(topicId).enc(privacy)
    }

    fun getPrivacy(): Privacy? {
        return textOfPrivacy?.let {
            Crypto(topicId).dec(it, Privacy::class.java)
        }
    }

    class Product(
        val title: String,
        val contents: List<SimpleContent>,
        val goods: List<Goods>?,
        val id: Long? = null,
        val price: Long? = null,
        val description: String? = null,
        val reservationDate: LocalDateTime? = null,
        val preDefineField: Map<String, Any?>? = null
    ) {

        data class Goods(
            val name: String,
            val quantity: Int,
            val id: Long? = null,
            val priceOfUnit: Long? = null,
            val discountPriceOfUnit: Long? = null,
            val description: String? = null,
            val goodsOptions: List<GoodsOption>? = null
        ) {

            data class GoodsOption(
                val name: String,
                var description: String? = null,
                val price: Long? = null,
                val id: Long? = null,
            )

            fun getTotalPrice(): Long {
                val priceOfGoods = ((priceOfUnit ?: 0) - (discountPriceOfUnit ?: 0)) * quantity
                val priceOfOption = goodsOptions?.sumOf { it.price ?: 0 } ?: 0
                return priceOfGoods + priceOfOption
            }
        }

        fun getTotalPrice(): Long {
            val priceOfGoods = goods?.sumOf { it.getTotalPrice() } ?: 0
            return priceOfGoods + (price ?: 0)
        }
    }

    fun getPriceOfOrder(): Long {
        return products.sumOf { it.getTotalPrice() }
    }

    data class PolicyCapture(
        val captureDate: LocalDateTime,
        val snapShotPolicyCards: List<PolicyCard>
    )

    fun capture(captureDate: LocalDateTime, snapShotPolicyCards: List<PolicyCard>) {
        if (Status.ORDERED != status)
            throw IllegalStateException("This is only possible when the order has been placed.")
        this.policyCapture = PolicyCapture(captureDate, snapShotPolicyCards)
    }

    fun setUsingPayMethod(usingPayMethod: UsingPayMethod) {
        if (this.estimateUsingPayMethod != null)
            throw IllegalStateException("Payment method information has already been set.")

        this.estimateUsingPayMethod = usingPayMethod
    }

    fun changeUsingPayMethod(usingPayMethod: UsingPayMethod) {
        if (this.estimateUsingPayMethod == null)
            throw IllegalStateException("Payment method information must be set first.")
        this.estimateUsingPayMethod = usingPayMethod
    }

    fun availableOrThrow() {
        if (status != Status.WAIT)
            throw IllegalStateException("Unavailable order")
        if (ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now()) > 360)
            throw CheckoutTimeoutException("Expired Checkout")
        this.estimateUsingPayMethod?.validOrThrow()
    }

    fun ordered() {
        if (Status.ORDERED == status)
            throw IllegalStateException("Your order has already been placed.")
        if (Status.FAIL == status)
            throw IllegalStateException("Your order has already failed.")

        status = Status.ORDERED
        modifiedAt = LocalDateTime.now()
    }

    fun writeSnapShot(policyCapture: PolicyCapture) {
        this.policyCapture = policyCapture
        this.modifiedAt = LocalDateTime.now()
    }

    fun hasPolicySnapShot(): Boolean {
        return (policyCapture != null)
    }

    fun failed() {
        if (Status.ORDERED == status)
            throw IllegalStateException("The order has already been approved.")

        this.status = Status.FAIL
        this.modifiedAt = LocalDateTime.now()
    }
}
