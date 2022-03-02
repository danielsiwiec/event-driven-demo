package com.dansiwiec.orders

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class OrdersService

fun main(args: Array<String>) {
    runApplication<OrdersService>(*args)
}
