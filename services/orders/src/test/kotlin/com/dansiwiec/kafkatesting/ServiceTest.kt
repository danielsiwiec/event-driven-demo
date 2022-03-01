package com.dansiwiec.kafkatesting

import com.dansiwiec.kafkatesting.models.LineItem
import com.dansiwiec.kafkatesting.models.Order
import com.dansiwiec.kafkatesting.models.OrderRequest
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
    topics = [Topics.ORDERS],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    brokerProperties = ["log.dir=build/embedded-kafka"]
)
class ServiceTest {

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    lateinit var consumer: Consumer<String, Order>

    @BeforeEach
    fun init() {
        consumer = testConsumer(embeddedKafkaBroker)
    }

    @Test
    fun testCreateOrder() {
        restTemplate.postForEntity("/orders", OrderRequest(items=listOf(LineItem(1, 1), LineItem(3, 2))), Order::class.java)
        val singleRecord = KafkaTestUtils.getSingleRecord(consumer, Topics.ORDERS)
        assertThat(singleRecord.key(), equalTo("0"))
        assertThat(singleRecord.value().id, equalTo(0))
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