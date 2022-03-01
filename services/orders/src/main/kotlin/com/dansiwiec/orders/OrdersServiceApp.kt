package com.dansiwiec.orders

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrdersService

fun main(args: Array<String>) {
    runApplication<OrdersService>(*args)
}
