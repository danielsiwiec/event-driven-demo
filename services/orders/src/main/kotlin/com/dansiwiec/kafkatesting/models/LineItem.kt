package com.dansiwiec.kafkatesting.models

data class LineItem(val id: Int, val quantity: Int) {
    constructor(): this(0, 0)
}