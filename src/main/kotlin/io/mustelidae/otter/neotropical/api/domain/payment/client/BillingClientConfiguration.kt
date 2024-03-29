package io.mustelidae.otter.neotropical.api.domain.payment.client

import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.BillingPaymentMethodClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.DummyBillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.DummyBillingPaymentMethodClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.StableBillingPayClient
import io.mustelidae.otter.neotropical.api.domain.payment.client.billing.StableBillingPaymentMethodClient
import io.netty.channel.ChannelOption
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class BillingClientConfiguration(
    private val appEnvironment: AppEnvironment
) {

    @Bean
    fun billingPayClient(): BillingPayClient {
        val billingEnv = appEnvironment.client.billing

        return if (billingEnv.useDummy)
            DummyBillingPayClient()
        else {
            val httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, billingEnv.timeout)
                .responseTimeout(Duration.ofMillis(billingEnv.timeout.toLong()))

            val webClient = WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(httpClient.wiretap(true)))
                .baseUrl(billingEnv.host)
                .build()

            StableBillingPayClient(webClient)
        }
    }

    @Bean
    fun billingPaymentMethodClient(): BillingPaymentMethodClient {
        val billingEnv = appEnvironment.client.billing

        return if (billingEnv.useDummy)
            DummyBillingPaymentMethodClient()
        else
            StableBillingPaymentMethodClient()
    }
}
