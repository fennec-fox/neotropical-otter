package io.mustelidae.otter.neotropical.api.domain.payment.voucher.client

import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class VoucherClientConfiguration(
    private val appEnvironment: AppEnvironment
) {

    @Bean
    fun voucherClient(): VoucherClient {
        return if (appEnvironment.client.voucher.useDummy)
            DummyVoucherClient()
        else
            StableVoucherClient()
    }
}
