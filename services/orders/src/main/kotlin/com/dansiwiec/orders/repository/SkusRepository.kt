package com.dansiwiec.orders.repository

import com.dansiwiec.orders.Topics
import com.dansiwiec.orders.models.Sku
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.stereotype.Component

@Component
class SkusRepository {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    val skus = mutableSetOf<String>()

    @KafkaListener(id = "order-service", topics = [Topics.SKUS])
    fun listenToSkus(sku: Sku) {
        logger.debug("Registering SKU ${sku.id}")
        skus.add(sku.id)
    }

    fun isValid(sku: Int): Boolean = skus.contains(sku.toString())
}