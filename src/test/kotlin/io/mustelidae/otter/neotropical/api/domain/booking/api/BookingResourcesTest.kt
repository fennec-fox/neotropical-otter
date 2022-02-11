package io.mustelidae.otter.neotropical.api.domain.booking.api

import kotlin.random.Random

internal class BookingResourcesTest

fun BookingResources.Request.Companion.aFixturePrePayOfOnlyCredit(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        "24273420fs3r2",
        null,
        null,
        null
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfVoucher(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        null,
        null,
        null,
        BookingResources.Request.Voucher(Random.nextLong(), Random.nextLong())
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfCreditWithPoint(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        "23423523422",
        1000,
        null,
        null
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfPointDiscount(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        null,
        2000,
        BookingResources.Request.Coupon(3),
        null
    )
}

fun BookingResources.Request.Companion.aFixturePrePayOfCreditWithDiscount(orderId: String): BookingResources.Request.PrePayBook {
    return BookingResources.Request.PrePayBook(
        orderId,
        "23423527s3427",
        null,
        BookingResources.Request.Coupon(1),
        null
    )
}

fun BookingResources.Request.Companion.aFixturePostPayOfCredit(orderId: String): BookingResources.Request.PostPayBook {
    return BookingResources.Request.PostPayBook(
        orderId,
        "jr32o0873whc",
        null,
        null,
        null
    )
}

fun BookingResources.Request.Companion.aFixturePostPayOfCreditWithDiscount(orderId: String): BookingResources.Request.PostPayBook {
    return BookingResources.Request.PostPayBook(
        orderId,
        "jr32o0873whc",
        null,
        BookingResources.Request.Coupon(1),
        null
    )
}

fun BookingResources.Request.Companion.aFixturePostPayOfVoucher(orderId: String): BookingResources.Request.PostPayBook {
    return BookingResources.Request.PostPayBook(
        orderId,
        "jr32o0873whc",
        null,
        null,
        BookingResources.Request.Voucher(Random.nextLong(), Random.nextLong())
    )
}
