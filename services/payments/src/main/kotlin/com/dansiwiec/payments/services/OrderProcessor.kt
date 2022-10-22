package com.dansiwiec.payments.services

import com.dansiwiec.payments.Topics
import com.dansiwiec.payments.models.Order
import com.dansiwiec.payments.models.Payment
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException

    @Service
    class OrderProcessor(
        @Autowired private val pricingService: PricingService,
        @Autowired private val paymentGatewayService: PaymentGatewayService,
        @Autowired private val kafkaTemplate: KafkaTemplate<Any, Any>
    ) {
        var logger = LoggerFactory.getLogger(this::class.java)!!

        @KafkaListener(id = "payment-service-orders", topics = [Topics.ORDERS])
        fun receiveOrder(order: Order) {
            val totalPrice = pricingService.calculatePrice(order)
            try {
                paymentGatewayService.submitPayment(order.customer, totalPrice)
                kafkaTemplate.send(Topics.PAYMENTS, order.id, Payment(order.id, Payment.Status.PAID))
                logger.info("Processed order ${order.id}")
            } catch (e: RestClientException) {
                kafkaTemplate.send(Topics.PAYMENTS, order.id, Payment(order.id, Payment.Status.FAILED))
                logger.warn("Payment failed for order ${order.id}: {}", e.message)
            }
        }
    }