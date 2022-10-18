package com.dansiwiec.payments.models

data class LineItem(val sku: String, val quantity: Int) {
    constructor(): this("", 0)
}