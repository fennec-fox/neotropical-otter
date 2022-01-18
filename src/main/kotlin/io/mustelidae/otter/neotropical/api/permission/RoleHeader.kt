package io.mustelidae.otter.neotropical.api.permission

object RoleHeader {

    object XAdmin {
        const val NAME = "어드민 ID"
        const val KEY = "x-admin-id"
        const val DATA_TYPE = "long"
        const val PARAM_TYPE = "header"
    }

    object XPartner {
        const val NAME = "파트너사 ID"
        const val KEY = "x-partner-id"
        const val DATA_TYPE = "long"
        const val PARAM_TYPE = "header"
    }

    object XUser {
        const val NAME = "사용자 ID"
        const val KEY = "x-user-id"
        const val DATA_TYPE = "long"
        const val PARAM_TYPE = "header"
    }
}
