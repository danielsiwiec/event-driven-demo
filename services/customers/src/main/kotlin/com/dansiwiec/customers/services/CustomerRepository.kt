package com.dansiwiec.customers.services

import com.dansiwiec.customers.Topics
import com.dansiwiec.customers.models.Customer
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CustomerRepository(val template: KafkaTemplate<String, Customer>) {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    companion object {
        val customers = setOf(
            Customer(id = "1", name = "John Doe", accountId = "123", email = "john.doe@gmail.com"),
            Customer(id = "2", name = "Jane Smith", accountId = "345", email = "jane.smith@yahoo.com"),
            Customer(id = "3", name = "Mark Novak", accountId = "678", email = "mark@novak.com"),
            Customer(id = "4", name = "Ana Mendez", accountId = "901", email = "ana.medndez@gmail.com"),
        )
    }

    @PostConstruct
    fun publishCustomers() {
        logger.info("Publishing Customers")
        customers.forEach { customer -> template.send(Topics.CUSTOMERS, customer.id, customer)}
    }
}