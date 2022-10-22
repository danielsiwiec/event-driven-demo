package com.dansiwiec.payments.models

data class Order(var id: String = "0", var items: List<LineItem>, var customer: String) {

    constructor(): this("0", emptyList(), "")
}