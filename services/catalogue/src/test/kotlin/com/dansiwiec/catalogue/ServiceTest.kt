package com.dansiwiec.catalogue

import com.dansiwiec.catalogue.controllers.SkuRepository
import com.dansiwiec.catalogue.models.Sku
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
        consumer.subscribe(listOf(Topics.SKUS))
    }

    @Test
    fun publishAllSkus() {
        val skuListSize = SkuRepository.catalogue.size
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(5).toMillis(), skuListSize)
        val value = records.iterator().next().value() as Sku
        print("SKU ${value.name}")
        assertThat(records.count(), equalTo(skuListSize))
    }
}