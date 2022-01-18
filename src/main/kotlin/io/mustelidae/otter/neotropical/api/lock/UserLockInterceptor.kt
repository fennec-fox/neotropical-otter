package io.mustelidae.otter.neotropical.api.lock

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UserLockInterceptor
@Autowired constructor(
    private val stringRedisTemplate: StringRedisTemplate
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val userId = request.getHeader(RoleHeader.XUser.KEY) ?: return true
        if (handler is HandlerMethod && handler.hasMethodAnnotation(EnableUserLock::class.java)) {
            val key = LockRedisKey(userId).getKey()

            if (stringRedisTemplate.hasKey(key)) {
                throw PolicyException(Error(ErrorCode.PL01, "이미 주문이 진행중입니다. 5분 뒤에 다시 시도를 부탁드립니다."))
            }
            stringRedisTemplate.opsForValue()
                .setIfAbsent(key, LocalDateTime.now().toString(), Duration.ofMinutes(5L))
        }

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler is HandlerMethod && handler.hasMethodAnnotation(EnableUserLock::class.java)) {
            val userId = request.getHeader(RoleHeader.XUser.KEY) ?: return
            val key = LockRedisKey(userId).getKey()
            stringRedisTemplate.delete(key)
        }
    }
}
