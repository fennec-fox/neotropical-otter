package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.api.common.Constant
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.testcontainers.containers.GenericContainer
import java.io.IOException
import java.net.URISyntaxException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Lazy(false)
@TestConfiguration
class DefaultEmbeddedRedis(
    private val properties: RedisProperties
) {
    private var redis: GenericContainer<Nothing>? = null
    @PostConstruct
    @Throws(IOException::class, URISyntaxException::class)
    fun startRedis() {
        redis = GenericContainer<Nothing>("redis:5.0.13-alpine")
            .apply {
                withExposedPorts(6379)
            }

        redis!!.start()
    }

    @PreDestroy
    fun stopRedis() {
        redis!!.stop()
    }

    @Bean
    fun redisClusterConfiguration(): RedisClusterConfiguration {
        return RedisClusterConfiguration(
            listOf("${properties.host}:${redis!!.firstMappedPort}")
        )
    }

    @Bean(name = [Constant.Redis.USER_LOCK])
    fun userLockRedisTemplate(): StringRedisTemplate {
        val configuration = RedisStandaloneConfiguration(properties.host, redis!!.firstMappedPort)
        val factory = LettuceConnectionFactory(configuration).apply {
            afterPropertiesSet()
        }

        return StringRedisTemplate().apply {
            setConnectionFactory(factory)
        }
    }
}
