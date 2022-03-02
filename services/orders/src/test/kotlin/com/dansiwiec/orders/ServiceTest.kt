package com.dansiwiec.orders

import com.dansiwiec.orders.models.LineItem
import com.dansiwiec.orders.models.Order
import com.dansiwiec.orders.models.OrderRequest
import com.dansiwiec.orders.models.Sku
import com.dansiwiec.orders.repository.SkusRepository
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration
import java.util.concurrent.TimeUnit


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(
    topics = [Topics.ORDERS],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    brokerProperties = ["log.dir=build/embedded-kafka"]
)
class ServiceTest {

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    lateinit var skusRepository: SkusRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Sku>

    lateinit var consumer: Consumer<String, Order>

    @BeforeEach
    fun init() {
        consumer = testConsumer(embeddedKafkaBroker)
    }

    @Test
    fun testCreateOrderHappyPath() {

        loadSkus(listOf("1", "3"))

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem(1, 1), LineItem(3, 2))), Order::class.java
        )
        val orderId = response.body!!.id

        assertThat(response.statusCode.value(), equalTo(200))
        val singleRecord = KafkaTestUtils.getSingleRecord(consumer, Topics.ORDERS)
        assertThat(singleRecord.key(), equalTo(orderId.toString()))
        assertThat(singleRecord.value().id, equalTo(orderId))
    }

    @Test
    fun testCreateOrderSkuNotFound() {

        loadSkus(listOf("2"))

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem(1, 1), LineItem(3, 2))), Order::class.java
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

    private fun testConsumer(embeddedKafka: EmbeddedKafkaBroker): Consumer<String, Order> {
        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka)
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        consumerProps[JsonDeserializer.TRUSTED_PACKAGES] = "com.dansiwiec.*"
        val cf = DefaultKafkaConsumerFactory<String, Order>(consumerProps)
        val consumer = cf.createConsumer()
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, Topics.ORDERS)
        return consumer
    }


}