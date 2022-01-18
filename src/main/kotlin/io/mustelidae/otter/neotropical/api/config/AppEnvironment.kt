package io.mustelidae.otter.neotropical.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app")
class AppEnvironment
