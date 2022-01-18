package io.mustelidae.otter.neotropical.api.config.redis

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableConfigurationProperties(value = [RedisProperties::class])
@EnableRedisRepositories
class RedisConfiguration(
    private val properties: RedisProperties
) {

    @Bean
    fun stringRedisTemplate(): StringRedisTemplate {
        val configuration = RedisClusterConfiguration(
            properties.cluster.nodes
        ).apply {
            this.password = RedisPassword.of(properties.password)
        }
        val factory = LettuceConnectionFactory(configuration).apply { afterPropertiesSet() }

        return StringRedisTemplate().apply {
            setConnectionFactory(factory)
        }
    }
}
