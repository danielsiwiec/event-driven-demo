package com.dansiwiec.customers

import com.dansiwiec.customers.services.CustomerRepository
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest : KafkaTestBase() {

    @BeforeEach
    fun init() {
        consumer.subscribe(listOf(Topics.CUSTOMERS))
    }

    @Test
    fun publishAllCustomers() {
        val customerListSize = CustomerRepository.customers.size
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis(), customerListSize)
        assertThat(records.count(), equalTo(customerListSize))
    }
}