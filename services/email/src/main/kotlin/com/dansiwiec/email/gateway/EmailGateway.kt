package com.dansiwiec.email.gateway

import com.dansiwiec.email.Topics
import com.dansiwiec.email.models.Order
import com.dansiwiec.email.models.Payment
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Service
@RestController
@RequestMapping("/sentEmailCount")
class EmailGateway {

    var logger = LoggerFactory.getLogger(this::class.java)!!
    var emailCount = 0

    @KafkaListener(id = "email-service-orders", topics = [Topics.ORDERS])
    fun sendOrderConfirmation(order: Order) {
        logger.info("Order ${order.id}: Sent an email confirming order creation")
        emailCount++
    }

    @KafkaListener(id = "email-service-payments", topics = [Topics.PAYMENTS])
    fun sendPaymentConfirmation(payment: Payment) {
        logger.info("Order ${payment.id}: Sent an email confirming successful payment")
        emailCount++
    }

    @GetMapping
    fun count():Int {
        return emailCount
    }

    @PostMapping("reset")
    fun reset() {
        emailCount = 0
    }
}