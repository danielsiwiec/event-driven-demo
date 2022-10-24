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
        @Autowired private val pricingCalculator: PricingCalculator,
        @Autowired private val paymentGatewayService: PaymentGatewayService,
        @Autowired private val kafkaTemplate: KafkaTemplate<Any, Any>
    ) {
        var logger = LoggerFactory.getLogger(this::class.java)!!

        @KafkaListener(id = "payment-service-orders", topics = [Topics.ORDERS])
        fun receiveOrder(order: Order) {
            try {
                val totalPrice = pricingCalculator.calculatePrice(order)
                paymentGatewayService.submitPayment(order.customerId, totalPrice)
                kafkaTemplate.send(Topics.PAYMENTS, order.id, Payment(order.id, Payment.Status.PAID))
                logger.info("Order ${order.id}: Processed payment")
            } catch (e: RestClientException) {
                kafkaTemplate.send(Topics.PAYMENTS, order.id, Payment(order.id, Payment.Status.FAILED))
                logger.warn("Order ${order.id}: Payment failed: {}", e.message)
            }
        }
    }