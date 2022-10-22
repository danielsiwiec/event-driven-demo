package com.dansiwiec.orders.models

data class Order(val id: String = "", val items: List<LineItem>, val customerId: String) {

    constructor(): this("", emptyList(), "")

    companion object {
        var currenOrderId = 0

        fun toOrder(wireType: OrderRequest): Order {
            return Order(currenOrderId++.toString(), wireType.items, wireType.customerId)
        }
    }
}