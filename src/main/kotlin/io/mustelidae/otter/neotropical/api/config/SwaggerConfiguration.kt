package io.mustelidae.otter.neotropical.api.config

import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {

    @Bean
    fun default(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("API")
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("(?!/maintenance).+")
        .build()

    @Bean
    fun maintenance(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Maintenance")
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/maintenance/**")
        .build()

    @Bean
    fun migration(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Migration")
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/migration/**")
        .build()

    @Bean
    fun bridge(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Bridge")
        .packagesToScan("io.mustelidae.otter.neotropical.api.domain")
        .pathsToMatch("/bridge/**")
        .build()

    @Bean
    fun uiConfig(): OpenAPI = OpenAPI().components(
        Components().addSchemas(
            "GlobalError", ModelConverters.getInstance().resolveAsResolvedSchema(AnnotatedType(GlobalErrorFormat::class.java)).schema
        )
    )
}

@Schema(description = "Common Error format")
data class GlobalErrorFormat(
    val timestamp: String,
    @Schema(name = "Http Status Code")
    val status: Int,
    @Schema(name = "error code")
    val code: String,
    @Schema(name = "text displayed to the user")
    val description: String,
    @Schema(name = "exception message")
    val message: String,
    @Schema(name = "exception name")
    val type: String,
    @Schema(name = "reference error code")
    val refCode: String
)
