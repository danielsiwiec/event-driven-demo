package com.dansiwiec.orders

import com.dansiwiec.orders.models.*
import com.dansiwiec.orders.repository.CustomersRepository
import com.dansiwiec.orders.repository.SkusRepository
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest : KafkaTestBase() {

    @Autowired
    lateinit var skusRepository: SkusRepository

    @Autowired
    lateinit var customersRepository: CustomersRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    fun init() {
        await().atMost(10, TimeUnit.SECONDS).until { skusRepository.skus.isNotEmpty() }
        await().atMost(10, TimeUnit.SECONDS).until { customersRepository.customers.isNotEmpty() }
        consumer.subscribe(listOf(Topics.ORDERS))
    }

    @Test
    fun testCreateOrderHappyPath() {
        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem("1", 1), LineItem("3", 2)), customerId = "1"), Order::class.java
        )
        val orderId = response.body!!.id

        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        val singleRecord = KafkaTestUtils.getSingleRecord(consumer, Topics.ORDERS)
        assertThat(singleRecord.key(), equalTo(orderId))

        val value = singleRecord.value() as Order
        assertThat(value.id, equalTo(orderId))
    }

    @Test
    fun testCreateOrderSkuNotFound() {
        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem("1", 1), LineItem("5", 2)), customerId = "1"), Order::class.java
        )
        assertThat(response.statusCode, equalTo(HttpStatus.BAD_REQUEST))
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())
        assertThat(records.count(), equalTo(0))
    }

    @Test
    fun testCreateOrderCustomerNotFound() {
        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem("1", 1), LineItem("3", 2)), customerId = "2"), Order::class.java
        )
        assertThat(response.statusCode, equalTo(HttpStatus.BAD_REQUEST))
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())
        assertThat(records.count(), equalTo(0))
    }

    @Test
    fun testCreateOrderNoLineItems() {
        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = emptyList(), customerId = "1"), Order::class.java
        )
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())

        assertThat(response.statusCode, equalTo(HttpStatus.BAD_REQUEST))
        assertThat(records.count(), equalTo(0))
    }

    @Test
    fun testCreateOrderNoCustomer() {
        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem("1", 1), LineItem("3", 2)), customerId = ""), Order::class.java
        )
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())

        assertThat(response.statusCode, equalTo(HttpStatus.BAD_REQUEST))
        assertThat(records.count(), equalTo(0))
    }
}

@Configuration
class TestData {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @PostConstruct
    fun loadData() {
        loadSkus(listOf("1", "2", "3"))
        loadCustomers(listOf("1"))
    }

    private fun loadSkus(skus: List<String>) {
        skus.forEach { kafkaTemplate.send(Topics.SKUS, it, Sku(it)) }
    }

    private fun loadCustomers(customers: List<String>) {
        customers.forEach { kafkaTemplate.send(Topics.CUSTOMERS, it, Customer(it)) }
    }
}