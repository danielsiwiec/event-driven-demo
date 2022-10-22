package com.dansiwiec.payments.services

import com.dansiwiec.payments.Topics
import com.dansiwiec.payments.models.Customer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class CustomerRepo {

    var logger = LoggerFactory.getLogger(this::class.java)!!
    val customers = mutableMapOf<String, Customer>()


    fun lookup(id: String): Customer? {
        return customers[id]
    }

    @KafkaListener(id = "order-service-customers", topics = [Topics.CUSTOMERS])
    fun listenToSkus(customer: Customer) {
        logger.debug("Registering Customer ${customer.id}")
        customers[customer.id] = customer
    }

}
