package io.mustelidae.otter.neotropical.api.permission

import io.mustelidae.otter.neotropical.api.config.DevelopMistakeException
import io.mustelidae.otter.neotropical.api.config.MissingRequestXHeaderException
import io.mustelidae.otter.neotropical.api.config.PermissionException
import io.mustelidae.otter.neotropical.api.domain.order.OrderSheet
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class DataAuthentication(
    vararg requiredRoles: RoleHeader.Role
) {
    private var permissions: MutableList<Permission> = mutableListOf()

    fun validateOrThrow(orderSheet: OrderSheet) {
        for (permission in permissions) {
            if (permission.checkOrderSheet(orderSheet).not())
                throw PermissionException()
        }
    }

    init {
        if (RequestContextHolder.getRequestAttributes() == null)
            throw DevelopMistakeException("can't read requestAttributes")

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

        if (requiredRoles.hasRole(RoleHeader.XSystem)) {
            val id = request.getHeader(RoleHeader.XSystem.KEY)
            if (id.isNullOrBlank())
                throw MissingRequestXHeaderException("required ${RoleHeader.XSystem.NAME}. header ${RoleHeader.XSystem.KEY}")

            permissions.add(SystemPermission(id))
        }

        if (requiredRoles.hasRole(RoleHeader.XAdmin)) {
            val id = request.getHeader(RoleHeader.XAdmin.KEY)
            if (id.isNullOrBlank())
                throw MissingRequestXHeaderException("required ${RoleHeader.XAdmin.NAME}. header ${RoleHeader.XAdmin.KEY}")

            permissions.add(AdminPermission(id.toLong()))
        }

        if (requiredRoles.hasRole(RoleHeader.XPartner)) {
            val id = request.getHeader(RoleHeader.XPartner.KEY)
            if (id.isNullOrBlank())
                throw MissingRequestXHeaderException("required ${RoleHeader.XPartner.NAME}. header ${RoleHeader.XPartner.KEY}")

            permissions.add(PartnerPermission(id.toLong()))
        }

        if (requiredRoles.hasRole(RoleHeader.XUser)) {
            val id = request.getHeader(RoleHeader.XUser.KEY)
            if (id.isNullOrBlank())
                throw MissingRequestXHeaderException("required ${RoleHeader.XUser.NAME}. header ${RoleHeader.XUser.KEY}")

            permissions.add(UserPermission(id.toLong()))
        }

        if (permissions.isEmpty())
            throw MissingRequestXHeaderException("can't found request header by ${requiredRoles.joinToString { it::class.simpleName!! }}")
    }
}

private fun Array<out RoleHeader.Role>.hasRole(role: RoleHeader.Role): Boolean {
    return (this.find { it == role } != null)
}
