package com.dansiwiec.orders.repository

import com.dansiwiec.orders.Topics
import com.dansiwiec.orders.models.Customer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class CustomersRepository {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    val customers = mutableSetOf<String>()

    @KafkaListener(id = "order-service-customers", topics = [Topics.CUSTOMERS])
    fun listenToSkus(customer: Customer) {
        logger.debug("Registering Customer ${customer.id}")
        customers.add(customer.id)
    }

    fun isValid(customer: String): Boolean = customers.contains(customer)
}