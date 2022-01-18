package io.mustelidae.otter.neotropical

import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppEnvironment::class)
class NeotropicalOtterApplication

fun main(args: Array<String>) {
    runApplication<NeotropicalOtterApplication>(*args)
}
