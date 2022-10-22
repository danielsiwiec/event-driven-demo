package com.dansiwiec.payments

import com.dansiwiec.payments.models.Payment
import com.github.tomakehurst.wiremock.WireMockServer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

    @Testcontainers
    @ContextConfiguration(initializers = [TestBase.Companion.TestContainerInitializer::class])
    @AutoConfigureWireMock(port = 0)
    abstract class TestBase {

        @Autowired lateinit var kafkaTemplate: KafkaTemplate<String, Any>
        @Autowired lateinit var wireMockServer: WireMockServer

        companion object {
            @Container
            val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.2.arm64"))
            lateinit var consumer: Consumer<String, Payment>

            private fun createConsumer(): Consumer<String, Payment> {
                val consumerProps = KafkaTestUtils.consumerProps(kafka.bootstrapServers, "testGroup", "false")
                consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
                consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
                consumerProps[JsonDeserializer.TRUSTED_PACKAGES] = "com.dansiwiec.*"
                val cf = DefaultKafkaConsumerFactory<String, Payment>(consumerProps)
                return cf.createConsumer()
            }

            class TestContainerInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
                override fun initialize(applicationContext: ConfigurableApplicationContext) {
                    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                        applicationContext,
                        "spring.kafka.bootstrap-servers=${kafka.bootstrapServers}"
                    )
                    consumer = createConsumer()
                    consumer.subscribe(listOf(Topics.PAYMENTS))
                }
            }
        }
    }