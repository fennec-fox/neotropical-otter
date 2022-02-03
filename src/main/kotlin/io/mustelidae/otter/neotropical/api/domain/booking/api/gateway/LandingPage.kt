package io.mustelidae.otter.neotropical.api.domain.booking.api.gateway

import io.mustelidae.otter.neotropical.api.common.ProductCode
import io.mustelidae.otter.neotropical.api.config.AppEnvironment

class LandingPage(
    private val productCode: ProductCode,
    private val topicId: String,
    appEnv: AppEnvironment
) {
    private val topic: AppEnvironment.Product.Topic = appEnv.products
        .find { it.productCode == productCode }?.topics?.find { it.id == topicId }!!

    fun getActiveDetail(): String? = topic.landing.activeDetail
    fun getRecordDetail(): String? = topic.landing.recordDetail
    fun getLandingWay(): LandingWay = topic.landingWay
}
