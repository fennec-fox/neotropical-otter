package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import org.springframework.stereotype.Service

@Service
class VerticalClientHandler(
    private val appEnvironment: AppEnvironment
) {

    fun choiceClient(productCode: ProductCode): VerticalClient {

        return when (productCode) {
            ProductCode.MOCK_UP -> {
                val productEnv = appEnvironment.products.find { it.productCode == productCode }!!
                DummyVerticalClient(productEnv)
            }
        }
    }
}
