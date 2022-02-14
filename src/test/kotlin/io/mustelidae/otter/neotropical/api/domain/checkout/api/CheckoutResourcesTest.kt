package io.mustelidae.otter.neotropical.api.domain.checkout.api

import io.mustelidae.otter.neotropical.api.common.design.SimpleContent
import io.mustelidae.otter.neotropical.api.common.design.v1.component.Label
import java.time.LocalDateTime
import kotlin.random.Random

internal class CheckoutResourcesTest

fun CheckoutResources.Request.Checkout.Companion.aFixtureByOnce(userId: Long): CheckoutResources.Request.Checkout {
    return CheckoutResources.Request.Checkout(
        userId,
        listOf(
            CheckoutResources.Request.Checkout.ProductOrder(
                "Test Product",
                listOf(
                    SimpleContent(
                        Label("place", false), listOf("garden", "light"),
                    ),
                    SimpleContent(
                        Label("date", false), listOf("2021-12.13", "Sunday")
                    )
                ),
                listOf(
                    CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                        "A goods",
                        1,
                        Random.nextLong(1000, 100000),
                        1000
                    ),
                    CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                        "B goods",
                        2,
                        Random.nextLong(1000, 100000),
                        2000,
                        goodsOptionOrders = listOf(
                            CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder.GoodsOptionOrder(
                                "B-Option",
                                price = 500
                            )
                        )
                    )
                ),
                reservationDate = LocalDateTime.now().plusDays(1),
                preDefineField = mapOf(
                    "storeId" to 1,
                    "chargerId" to 234
                ),
                id = Random.nextLong(1000, 100000)
            )
        ),
        Random.nextLong(1000, 100000),
        mapOf(
            "type" to "NONE"
        )
    )
}

fun CheckoutResources.Request.Checkout.Companion.aFixtureByMultiProduct(userId: Long): CheckoutResources.Request.Checkout {
    return CheckoutResources.Request.Checkout(
        userId,
        listOf(
            CheckoutResources.Request.Checkout.ProductOrder(
                "Test A Product",
                listOf(
                    SimpleContent(
                        Label("A place", false), listOf("garden", "light"),
                    ),
                    SimpleContent(
                        Label("date", false), listOf("2021-12.13", "Sunday")
                    )
                ),
                listOf(
                    CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                        "A-1 goods",
                        1,
                        Random.nextLong(1000, 100000),
                        1000
                    ),
                    CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                        "A-2 goods",
                        2,
                        Random.nextLong(1000, 100000),
                        2000,
                        goodsOptionOrders = listOf(
                            CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder.GoodsOptionOrder(
                                "A-Option",
                                price = 500
                            )
                        )
                    )
                ),
                reservationDate = LocalDateTime.now().plusDays(1),
                preDefineField = mapOf(
                    "storeId" to 1,
                    "chargerId" to 234
                ),
                id = Random.nextLong(1000, 100000)
            ),
            CheckoutResources.Request.Checkout.ProductOrder(
                "Test B Product",
                listOf(
                    SimpleContent(
                        Label("B place", false), listOf("garden", "light"),
                    ),
                    SimpleContent(
                        Label("date", false), listOf("2022-01-24", "Sunday")
                    )
                ),
                listOf(
                    CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                        "B-1 goods",
                        1,
                        Random.nextLong(1000, 100000),
                        1000
                    ),
                    CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder(
                        "B-2 goods",
                        2,
                        Random.nextLong(1000, 100000),
                        2000,
                        goodsOptionOrders = listOf(
                            CheckoutResources.Request.Checkout.ProductOrder.GoodsOrder.GoodsOptionOrder(
                                "B-Option",
                                price = 500
                            )
                        )
                    )
                ),
                reservationDate = LocalDateTime.now().plusDays(1),
                preDefineField = mapOf(
                    "storeId" to 1,
                    "chargerId" to 234
                ),
                id = Random.nextLong(1000, 100000)
            )
        ),
        Random.nextLong(1000, 100000),
        mapOf(
            "type" to "NONE"
        )
    )
}

fun CheckoutResources.Request.Checkout.getTotalPrice(): Long {
    var totalPrice: Long = 0
    for (product in products) {

        for (goods in product.goodsOrders ?: emptyList()) {
            val priceOfOption = goods.goodsOptionOrders?.sumOf { it.price ?: 0 } ?: 0
            val priceOfGoods = ((goods.priceOfUnit ?: 0) - (goods.discountPriceOfUnit ?: 0)) * goods.quantity
            totalPrice += (priceOfGoods + priceOfOption)
        }
        totalPrice += product.price ?: 0
    }

    return totalPrice
}
