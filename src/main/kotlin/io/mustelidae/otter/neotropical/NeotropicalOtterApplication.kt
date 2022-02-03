package io.mustelidae.otter.neotropical

import io.mustelidae.otter.neotropical.api.config.AppEnvironment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator

@SpringBootApplication
@EnableConfigurationProperties(AppEnvironment::class)
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator::class)
class NeotropicalOtterApplication

fun main(args: Array<String>) {
    runApplication<NeotropicalOtterApplication>(*args)
}
