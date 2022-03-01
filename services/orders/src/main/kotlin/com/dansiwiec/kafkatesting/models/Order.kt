package com.dansiwiec.kafkatesting.models

data class Order(var id: Int = 0, var items: List<LineItem>) {

    constructor(): this(0, emptyList())

    companion object {
        var currenOrderId = 0

        fun toOrder(wireType: OrderRequest): Order {
            return Order(currenOrderId++, wireType.items)
        }
    }
}