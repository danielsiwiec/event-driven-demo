package com.dansiwiec.models

data class Order(val items: List<LineItem>, val customerId: String) {
    constructor(): this(emptyList(), "")
}

data class LineItem(val sku: String, val quantity: Int) {
    constructor(): this("", 0)
}