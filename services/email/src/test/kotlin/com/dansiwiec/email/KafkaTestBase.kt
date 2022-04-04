package com.dansiwiec.email

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@ContextConfiguration(initializers = [KafkaTestBase.Companion.TestContainerInitializer::class])
abstract class KafkaTestBase {

    companion object {
        @Container
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"))

        lateinit var consumer: Consumer<String, Any>

        private fun createConsumer(): Consumer<String, Any> {
            val consumerProps = KafkaTestUtils.consumerProps(kafka.bootstrapServers, "testGroup", "false")
            consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
            consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
            consumerProps[JsonDeserializer.TRUSTED_PACKAGES] = "com.dansiwiec.*"
            val cf = DefaultKafkaConsumerFactory<String, Any>(consumerProps)
            return cf.createConsumer()
        }

        class TestContainerInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(applicationContext: ConfigurableApplicationContext) {
                TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.kafka.bootstrap-servers=${kafka.bootstrapServers}"
                )
                consumer = createConsumer()
            }
        }
    }

}