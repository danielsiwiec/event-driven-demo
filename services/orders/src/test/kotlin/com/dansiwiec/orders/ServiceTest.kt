package com.dansiwiec.orders

import com.dansiwiec.orders.models.LineItem
import com.dansiwiec.orders.models.Order
import com.dansiwiec.orders.models.OrderRequest
import com.dansiwiec.orders.models.Sku
import com.dansiwiec.orders.repository.SkusRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration
import java.util.concurrent.TimeUnit


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest : KafkaTestBase() {

    @Autowired
    lateinit var skusRepository: SkusRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Sku>

    @BeforeEach
    fun init() {
        consumer.subscribe(listOf(Topics.ORDERS))
    }

    @Test
    fun testCreateOrderHappyPath() {

        loadSkus(listOf("1", "3"))

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem("1", 1), LineItem("3", 2))), Order::class.java
        )
        val orderId = response.body!!.id

        assertThat(response.statusCode.value(), equalTo(200))
        val singleRecord = KafkaTestUtils.getSingleRecord(consumer, Topics.ORDERS)
        assertThat(singleRecord.key(), equalTo(orderId.toString()))

        val value = singleRecord.value()
        if (value is Order) {
            assertThat(value.id, equalTo(orderId))
        } else {
            fail("Not Order type on Order topic")
        }

    }

    @Test
    fun testCreateOrderSkuNotFound() {

        loadSkus(listOf("2"))

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem("1", 1), LineItem("3", 2))), Order::class.java
        )
        assertThat(response.statusCode.value(), equalTo(400))
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())
        assertThat(records.count(), equalTo(0))
    }

    @Test
    fun testCreateOrderNoLineItems() {

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = emptyList()), Order::class.java
        )
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())

        assertThat(response.statusCode.value(), equalTo(400))
        assertThat(records.count(), equalTo(0))
    }

    private fun loadSkus(skus: List<String>) {
        skus.forEach { kafkaTemplate.send(Topics.SKUS, it, Sku(it)) }
        await().atMost(5, TimeUnit.SECONDS).until { skusRepository.skus.isNotEmpty() }
    }

}