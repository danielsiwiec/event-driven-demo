package com.dansiwiec.catalogue

import com.dansiwiec.catalogue.controllers.SkuRepository
import com.dansiwiec.catalogue.models.Sku
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import java.time.Duration


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(
    topics = [Topics.SKUS],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    brokerProperties = ["log.dir=build/embedded-kafka"]
)
class ServiceTest {

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    lateinit var consumer: Consumer<String, Sku>

    @BeforeEach
    fun init() {
        consumer = testConsumer(embeddedKafkaBroker)
    }

    @Test
    fun publishAllSkus() {
        val skuListSize = SkuRepository.catalogue.size
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis(), skuListSize)
        assertThat(records.count(), equalTo(skuListSize))
    }

    private fun testConsumer(embeddedKafka: EmbeddedKafkaBroker): Consumer<String, Sku> {
        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka)
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        consumerProps[JsonDeserializer.TRUSTED_PACKAGES] = "com.dansiwiec.*"
        val cf = DefaultKafkaConsumerFactory<String, Sku>(consumerProps)
        val consumer = cf.createConsumer()
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, Topics.SKUS)
        return consumer
    }
}