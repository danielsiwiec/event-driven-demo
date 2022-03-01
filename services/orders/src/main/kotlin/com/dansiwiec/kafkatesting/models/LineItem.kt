package com.dansiwiec.kafkatesting.models

data class LineItem(val sku: Int, val quantity: Int) {
    constructor(): this(0, 0)
}