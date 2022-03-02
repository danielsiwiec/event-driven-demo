package com.dansiwiec.models

data class Order(val items: List<LineItem>) {
    constructor(): this(emptyList())
}

data class LineItem(val sku: Int, val quantity: Int) {
    constructor(): this(0, 0)
}