package io.mustelidae.otter.neotropical.api.domain.vertical.client

import io.mustelidae.otter.neotropical.api.domain.vertical.BookingApproval
import io.mustelidae.otter.neotropical.api.domain.vertical.CancelPolicy
import io.mustelidae.otter.neotropical.api.domain.vertical.CancellationUnit
import io.mustelidae.otter.neotropical.api.domain.vertical.ExchangeResult
import io.mustelidae.otter.neotropical.api.domain.vertical.client.design.v1.VerticalRecord

interface VerticalClient : BookingApproval, CancelPolicy {

    fun cancel(userId: Long, bookingIds: List<Long>, cause: String): ExchangeResult

    fun cancelByItem(cancellationUnit: CancellationUnit, cause: String): ExchangeResult

    fun findRecord(bookingId: Long): VerticalRecord
}
