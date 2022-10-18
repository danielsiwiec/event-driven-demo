package com.dansiwiec.orders.models

data class Order(val id: Int = 0, val items: List<LineItem>, val customerId: String) {

    constructor(): this(0, emptyList(), "")

    companion object {
        var currenOrderId = 0

        fun toOrder(wireType: OrderRequest): Order {
            return Order(currenOrderId++, wireType.items, wireType.customerId)
        }
    }
}