package io.mustelidae.otter.neotropical.api.common

enum class ErrorCode(val summary: String) {

    H000("Human error"),

    HD00("Data not found"),
    HD01("No results found"),
    HD02("Data mismatch uri"),

    HA00("Unauthorized"),
    HA01("Unauthorized Data Relation"),

    HI00("Invalid Input"),
    HI01("Invalid argument"),
    HI02("Invalid header argument"),

    P000("policy error"),
    PC01("checkout error"),
    PC02("checkout timeout"),
    PD01("develop mistake error"),
    PD02("Found that a required value is missing"),
    PL01("duplicate request"),
    PL02("payment rollback error"),
    PP01("exceed the limit"),
    PP02("There are no items that can be canceled."),
    PP03("Vertical's Policy"),

    S000("common system error"),
    SA00("async execute error"),
    SI01("illegal state error"),
    SI02("database access error"),
    SD01("This error is a compilation error level. Check out the code"),

    C000("communication error"),
    CT01("connection timeout"),
    CT02("read timeout"),
}
