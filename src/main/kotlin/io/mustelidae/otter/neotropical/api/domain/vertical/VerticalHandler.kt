package io.mustelidae.otter.neotropical.api.domain.vertical

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import io.mustelidae.otter.neotropical.api.domain.vertical.client.DummyVerticalClient
import io.mustelidae.otter.neotropical.api.domain.vertical.client.StableMockUpClient
import io.mustelidae.otter.neotropical.api.domain.vertical.client.VerticalClient
import org.springframework.stereotype.Service

@Service
class VerticalHandler(
    private val appEnvironment: AppEnvironment
) {

    fun getBooking(orderSheet: OrderSheet): VerticalBooking {
        val productCode = orderSheet.productCode
        val verticalClient = getClient(productCode)
        return when (productCode) {
            ProductCode.MOCK_UP -> NormalBooking(verticalClient, orderSheet)
        }
    }

    fun getBooking(orderSheet: OrderSheet, bookings: List<Booking>): VerticalBooking {
        val productCode = bookings.first().productCode
        val verticalClient = getClient(productCode)
        return when (productCode) {
            ProductCode.MOCK_UP -> NormalBooking(verticalClient, orderSheet, bookings)
        }
    }

    private fun getClient(productCode: ProductCode): VerticalClient {

        return when (productCode) {
            ProductCode.MOCK_UP -> {
                val productEnv = appEnvironment.products.find { it.productCode == productCode }!!
                if (productEnv.useDummy)
                    DummyVerticalClient(productEnv)
                else
                    StableMockUpClient()
            }
        }
    }
}
