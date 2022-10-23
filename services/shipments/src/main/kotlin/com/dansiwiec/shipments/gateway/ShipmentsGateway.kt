package com.dansiwiec.shipments.gateway

import com.dansiwiec.shipments.Topics
import com.dansiwiec.shipments.models.Payment
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Service
@RestController
@RequestMapping("/shipmentsCount")
class ShipmentsGateway {

    var logger = LoggerFactory.getLogger(this::class.java)!!
    var shipmentsCount = 0

    @KafkaListener(id = "shipment-service-payments", topics = [Topics.PAYMENTS])
    fun sendPaymentConfirmation(payment: Payment) {
        if (payment.status == Payment.Status.PAID) {
            logger.info("Order ${payment.id}: Shipment has been dispatched")
            shipmentsCount++
        }
    }

    @GetMapping
    fun count():Int {
        return shipmentsCount
    }

    @PostMapping("reset")
    fun reset() {
        shipmentsCount = 0
    }
}