package com.dansiwiec.payments.gateway

import com.dansiwiec.payments.Topics
import com.dansiwiec.payments.models.Order
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Service
@RestController
@RequestMapping("/sentEmailCount")
class EmailGateway {

    var logger = LoggerFactory.getLogger(this::class.java)!!
    var emailCount = 0

    @KafkaListener(id = "email-service", topics = [Topics.ORDERS])
    fun sendOrderConfirmation(order: Order) {
        logger.info("Sending an email confirming order")
        emailCount++
    }

    @GetMapping
    fun count():Int {
        return emailCount
    }
}