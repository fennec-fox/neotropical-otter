package io.mustelidae.otter.neotropical.api.domain.order.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.order.OrderInteraction
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Order")
@RestController
@RequestMapping("/v1/product/{productCode}/order")
class OrderController(
    private val orderSheetFinder: OrderSheetFinder,
    private val orderInteraction: OrderInteraction
) {

    @GetMapping("{id}")
    fun findOne(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable productCode: ProductCode,
        @PathVariable id: String
    ): Reply<OrderResources.Reply.PurchaseOrder> {
        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(id))

        DataAuthentication(RoleHeader.XUser).validOrThrow(orderSheet)

        return OrderResources.Reply.PurchaseOrder.from(orderSheet)
            .toReply()
    }

    @PatchMapping("{id}")
    fun ackOrder(
        @RequestHeader(RoleHeader.XSystem.KEY) systemId: String,
        @PathVariable productCode: ProductCode,
        @RequestBody request: OrderResources.Modify.Order,
        @PathVariable id: String
    ): Reply<Unit> {
        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(id))
        DataAuthentication(RoleHeader.XSystem).validOrThrow(orderSheet)

        val orderId = ObjectId(id)
        when (request.status) {
            OrderSheet.Status.ORDERED -> orderInteraction.complete(orderId)
            OrderSheet.Status.FAIL -> orderInteraction.fail(orderId)
            else -> throw IllegalStateException("It cannot be changed to that state.")
        }
        return Unit.toReply()
    }
}
