package com.dansiwiec.kafkatesting.controllers

import com.dansiwiec.kafkatesting.Topics
import com.dansiwiec.kafkatesting.models.Order
import com.dansiwiec.kafkatesting.models.OrderRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(val template: KafkaTemplate<String, Order>) {

    var logger = LoggerFactory.getLogger(OrderController::class.java)!!

    @PostMapping
    fun createOrder(@RequestBody wireOrder: OrderRequest) : ResponseEntity<Order> {
        val order = Order.toOrder(wireOrder)
        logger.info("Creating order ${order.id}")
        template.send(Topics.ORDERS, order.id.toString(), order)
        return ResponseEntity.ok(order)
    }
}