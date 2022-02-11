package io.mustelidae.otter.neotropical.utils

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking

fun List<Booking>.sameOrThrow() {
    val codes = this.map { it.productCode }.toSet()
    if (codes.size > 1)
        throw PolicyException(Error(ErrorCode.PP04, "Not the same product code."))

    val payIds = this.map { it.payment?.id }.toSet()

    if (payIds.size > 2)
        throw PolicyException(Error(ErrorCode.PP04, "Not the same payment."))
}
