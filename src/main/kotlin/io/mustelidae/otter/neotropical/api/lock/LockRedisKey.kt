package io.mustelidae.otter.neotropical.api.lock

import io.mustelidae.otter.neotropical.api.config.redis.RedisKey

class LockRedisKey(
    private val userId: String
) : RedisKey {

    override fun getKey(): String {
        return KEY + userId
    }

    companion object {
        private const val KEY = "lock:user:"
    }
}
