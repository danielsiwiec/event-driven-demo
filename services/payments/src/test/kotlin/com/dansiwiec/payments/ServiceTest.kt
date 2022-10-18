package com.dansiwiec.payments

import com.dansiwiec.payments.models.LineItem
import com.dansiwiec.payments.models.Order
import com.dansiwiec.payments.Topics
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.KafkaTemplate
import java.time.Duration.ofSeconds

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest : KafkaTestBase() {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, Order>

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun testSendEmailWhenOrderCreated() {
        val order = Order(1, listOf(LineItem("1", 2)))
        kafkaTemplate.send(Topics.ORDERS, order.id.toString(), order)

        await().atMost(ofSeconds(10)).until( { testRestTemplate.getForObject("/sentEmailCount", Int::class.java)}, equalTo(1) )
    }

}