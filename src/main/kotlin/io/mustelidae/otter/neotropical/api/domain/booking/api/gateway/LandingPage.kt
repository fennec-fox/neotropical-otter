package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway

import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import io.mustelidae.otter.neotropical.api.domain.booking.Booking
import io.mustelidae.otter.neotropical.utils.UriStringMaker

class LandingPage(
    private val booking: Booking,
    appEnv: AppEnvironment
) {
    private val productEnv = appEnv.products.find { it.productCode == booking.productCode }
    private val topic: AppEnvironment.Product.Topic? = productEnv?.topics?.find { it.id == booking.topicId }

    fun getActiveDetail(): String? {
        if (productEnv == null || topic == null)
            return null

        return UriStringMaker.replace(productEnv.host, topic.landing.activeDetail!!, mapOf("bookingId" to booking.id!!))
    }
    fun getRecordDetail(): String? {
        if (productEnv == null || topic == null)
            return null

        return UriStringMaker.replace(productEnv.host, topic.landing.recordDetail!!, mapOf("bookingId" to booking.id!!))
    }
    fun getLandingWay(): LandingWay {
        return topic?.landingWay ?: LandingWay.NONE
    }
}
