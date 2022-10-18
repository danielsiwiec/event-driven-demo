package com.dansiwiec.orders.repository

import com.dansiwiec.orders.Topics
import com.dansiwiec.orders.models.Sku
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SkusRepository {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    val skus = mutableSetOf<String>()

    @KafkaListener(id = "order-service-skus", topics = [Topics.SKUS])
    fun listenToSkus(sku: Sku) {
        logger.debug("Registering SKU ${sku.id}")
        skus.add(sku.id)
    }

    fun isValid(sku: String): Boolean = skus.contains(sku)
}