package com.dansiwiec.kafkatesting

import com.dansiwiec.kafkatesting.models.LineItem
import com.dansiwiec.kafkatesting.models.Order
import com.dansiwiec.kafkatesting.models.OrderRequest
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.matchesRegex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.client.ExpectedCount.manyTimes
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseActions
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.RestTemplate
import java.time.Duration


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
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var restTemplate: RestTemplate

    @Value("\${catalogueService.url}")
    lateinit var catalogueService: String

    lateinit var consumer: Consumer<String, Order>
    lateinit var catalogueServiceResponse: ResponseActions

    @BeforeEach
    fun init() {
        consumer = testConsumer(embeddedKafkaBroker)
        catalogueServiceResponse = MockRestServiceServer.createServer(restTemplate)
            .expect(manyTimes(), requestTo(matchesRegex("$catalogueService/skus/\\d*")))
    }

    @Test
    fun testCreateOrderHappyPath() {
        catalogueServiceResponse.andRespond(withStatus(HttpStatus.OK))

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
        catalogueServiceResponse.andRespond(withStatus(HttpStatus.NOT_FOUND))

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = listOf(LineItem(1, 1), LineItem(3, 2))), Order::class.java
        )
        assertThat(response.statusCode.value(), equalTo(400))
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())
        assertThat(records.count(), equalTo(0))
    }

    @Test
    fun testCreateOrderNoLineItems() {
        catalogueServiceResponse.andRespond(withStatus(HttpStatus.NOT_FOUND))

        val response = testRestTemplate.postForEntity(
            "/orders", OrderRequest(items = emptyList()), Order::class.java
        )
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis())

        assertThat(response.statusCode.value(), equalTo(400))
        assertThat(records.count(), equalTo(0))
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