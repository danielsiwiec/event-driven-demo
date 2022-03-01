package com.dansiwiec.kafkatesting.models

data class OrderRequest(val items: List<LineItem>) {
    constructor(): this(emptyList())
}