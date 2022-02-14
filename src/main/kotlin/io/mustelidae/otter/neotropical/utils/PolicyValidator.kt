package io.mustelidae.otter.neotropical.utils

import io.mustelidae.otter.neotropical.api.common.Error
import io.mustelidae.otter.neotropical.api.common.ErrorCode
import io.mustelidae.otter.neotropical.api.config.PolicyException
import io.mustelidae.otter.neotropical.api.domain.booking.Booking

fun List<Booking>.sameOrThrow() {
    val setOfText = this.map { "${it.productCode}|${it.orderId}|${it.payment?.id}}" }.toSet()

    if (setOfText.size > 1)
        throw PolicyException(Error(ErrorCode.PP04, "Not the same order"))
}
