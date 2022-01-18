package io.mustelidae.otter.neotropical.api.config

import io.mustelidae.otter.neotropical.api.permission.RoleHeader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.Optional

@Configuration
class AuditConfiguration {

    @Bean
    internal fun auditorAware(): AuditorAware<*> {
        return AuditorAwareImpl()
    }
}

class AuditorAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        var auditor = unknownAuditor

        if (RequestContextHolder.getRequestAttributes() != null) {
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

            val adminId = request.getHeader(RoleHeader.XAdmin.KEY)
            val partnerId = request.getHeader(RoleHeader.XPartner.KEY)
            val userId = request.getHeader(RoleHeader.XUser.KEY)

            auditor = adminId?.let { "A:".plus(it) } ?: partnerId?.let { "P:".plus(it) } ?: userId?.let { "U:".plus(it) } ?: unknownAuditor
        }
        return Optional.of(auditor)
    }

    companion object {
        const val unknownAuditor = "S:unknown"
    }
}
