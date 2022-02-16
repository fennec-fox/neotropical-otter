package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.domain.booking.api.gateway.LandingWay
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppEnvironment {
    val client = Client()
    var products: List<Product> = emptyList()

    class Client {
        var billing = Billing()
        class Billing : ConnInfo()
        var voucher = Voucher()
        class Voucher : ConnInfo()
    }

    class Product : ConnInfo() {
        lateinit var productCode: ProductCode
        var dummyControl = DummyControl()
        var topics: List<Topic> = emptyList()

        class Topic {
            lateinit var id: String
            lateinit var landingWay: LandingWay
            var landing = Landing()

            class Landing {
                var activeDetail: String? = null
                var recordDetail: String? = null
            }
        }

        class DummyControl {
            var cancel: Boolean = true
            var obtainApproval: Boolean = true
            var cancelByItem: Boolean = true
            var callOff: Boolean = true
            var onAutoConfirm: Boolean = false
        }
    }

    open class ConnInfo {
        var host: String = "localhost"
        var timeout: Int = 5000
        var logging: Boolean = false
        var useDummy: Boolean = false
    }
}
