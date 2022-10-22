package com.dansiwiec.customers.services

import com.dansiwiec.customers.Topics
import com.dansiwiec.customers.models.Customer
import com.dansiwiec.customers.models.State
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CustomerRepository(val template: KafkaTemplate<String, Customer>) {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    companion object {
        val customers = setOf(
            Customer(id = "1", name = "John Doe", accountNumber = "123", email = "john.doe@gmail.com", state = State.CA),
            Customer(id = "2", name = "Jane Smith", accountNumber = "345", email = "jane.smith@yahoo.com", state = State.OR),
            Customer(id = "3", name = "Mark Novak", accountNumber = "678", email = "mark@novak.com", state = State.ME),
            Customer(id = "4", name = "Ana Mendez", accountNumber = "901", email = "ana.medndez@gmail.com", state = State.MN),
        )
    }

    @PostConstruct
    fun publishCustomers() {
        logger.info("Publishing Customers")
        customers.forEach { customer -> template.send(Topics.CUSTOMERS, customer.id, customer)}
    }
}