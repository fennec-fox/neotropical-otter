package io.mustelidae.otter.neotropical.api.domain.booking.api

import kotlin.random.Random

internal class BookingResourcesTest

fun BookingResources.Request.Companion.aFixturePrePayOfOnlyCredit(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        "24273420fs3r2",
        null,
        null,
        null,
        1
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfVoucher(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        null,
        null,
        null,
        BookingResources.Request.Voucher(Random.nextLong(), Random.nextLong()),
        2
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfCreditWithPoint(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        "23423523422",
        1000,
        null,
        null,
        3
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfPointDiscount(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        null,
        2000,
        BookingResources.Request.Coupon(Random.nextLong(), Random.nextLong()),
        null,
        4
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfCreditDiscount(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        "23423527s3427",
        null,
        BookingResources.Request.Coupon(Random.nextLong(), Random.nextLong()),
        null,
        5
    )
}
