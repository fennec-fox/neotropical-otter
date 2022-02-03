package io.mustelidae.otter.neotropical.api.domain.order.api

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.common.Reply
import io.mustelidae.otter.neotropical.api.common.toReply
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheetFinder
import io.mustelidae.otter.neotropical.api.permission.DataAuthentication
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Order")
@RestController
@RequestMapping("/v1/product/{productCode}/order")
class OrderController(
    private val orderSheetFinder: OrderSheetFinder
) {

    @GetMapping("{orderId}")
    fun findOne(
        @RequestHeader(RoleHeader.XUser.KEY) userId: Long,
        @PathVariable productCode: ProductCode,
        @PathVariable orderId: String
    ): Reply<OrderResources.Reply.PurchaseOrder> {
        val orderSheet = orderSheetFinder.findOneOrThrow(ObjectId(orderId))

        DataAuthentication(RoleHeader.XUser).validOrThrow(orderSheet)

        return OrderResources.Reply.PurchaseOrder.from(orderSheet)
            .toReply()
    }
}
