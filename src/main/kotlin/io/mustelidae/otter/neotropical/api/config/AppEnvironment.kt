package io.mustelidae.otter.neotropical.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppEnvironment {
    val client = Client()

    class Client {
        var billing = Billing()

        open class ConnInfo {
            var host: String = "localhost"
            var timeout: Int = 5000
            var logging: Boolean = false
            var useDummy: Boolean = false
        }

        class Billing : ConnInfo()
    }
}
