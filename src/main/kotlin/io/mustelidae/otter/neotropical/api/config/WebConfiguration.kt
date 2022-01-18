package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.utils.Jackson
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.format.support.FormattingConversionService
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import java.time.format.DateTimeFormatter

@Configuration
@ControllerAdvice
class WebConfiguration() : WebMvcConfigurationSupport() {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val objectMapper = Jackson.getMapper()
        converters.add(MappingJackson2HttpMessageConverter(objectMapper))
        super.configureMessageConverters(converters)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/swagger-ui/")
            .setViewName("forward:/swagger-ui/index.html")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
    }

    @Bean
    fun requestResponseLogFilter(): FilterRegistrationBean<RequestResponseLogFilter> {
        return FilterRegistrationBean<RequestResponseLogFilter>().apply {
            filter = RequestResponseLogFilter()
        }
    }

    @Bean
    override fun mvcConversionService(): FormattingConversionService {
        val conversionService = super.mvcConversionService()
        val dateTimeRegistrar = DateTimeFormatterRegistrar()
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ISO_DATE)
        dateTimeRegistrar.setTimeFormatter(DateTimeFormatter.ISO_TIME)
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME)
        dateTimeRegistrar.registerFormatters(conversionService)
        return conversionService
    }
}
