package com.dansiwiec.payments.models

data class Payment(val id: String, val status: Status) {

    constructor(): this("0", Status.PAID)

    enum class Status {
        PAID, FAILED
    }
}

