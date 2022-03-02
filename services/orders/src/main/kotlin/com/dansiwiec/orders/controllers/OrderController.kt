package com.dansiwiec.orders.controllers

import com.dansiwiec.orders.Topics
import com.dansiwiec.orders.models.Order
import com.dansiwiec.orders.models.OrderRequest
import com.dansiwiec.orders.repository.SkusRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/orders")
class OrderController(
    val template: KafkaTemplate<String, Order>,
    val skusRepository: SkusRepository
) {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    @PostMapping
    fun createOrder(@RequestBody @Valid wireOrder: OrderRequest): ResponseEntity<Order> {
        val order = Order.toOrder(wireOrder)
        logger.info("Creating order ${order.id}")
        validateOrder(order)
        template.send(Topics.ORDERS, order.id.toString(), order)
        return ResponseEntity.ok(order)
    }

    private fun validateOrder(order: Order) {
        order.items.find { !skusRepository.isValid(it.sku) }?.let { throw BadSkuException() }
    }
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "SKU invalid")
class BadSkuException : RuntimeException()