package com.dansiwiec.payments.models

data class Customer(val state: State = State.CA, val id: String = "", val accountNumber: Int = 0) {

    constructor(): this(State.NA, "")
}