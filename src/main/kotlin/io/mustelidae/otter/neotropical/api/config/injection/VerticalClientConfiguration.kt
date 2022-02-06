package io.mustelidae.otter.neotropical.api.config.injection

import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.domain.vertical.client.DummyVerticalClient
import io.mustelidae.otter.neotropical.api.domain.vertical.client.StableMockUpClient
import io.mustelidae.otter.neotropical.api.domain.vertical.client.VerticalClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class VerticalClientConfiguration(
    private val appEnvironment: AppEnvironment
) {

    @Bean
    fun mockUpClient(): VerticalClient {
        val productEnv = findEnv(ProductCode.MOCK_UP)
        return if (productEnv.useDummy)
            DummyVerticalClient(productEnv)
        else
            StableMockUpClient()
    }

    private fun findEnv(productCode: ProductCode): AppEnvironment.Product {
        return appEnvironment.products.find { it.productCode == productCode } ?: throw DevelopMistakeException(ErrorCode.SD01)
    }
}
