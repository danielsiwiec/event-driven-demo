package com.dansiwiec.payments.services

import com.dansiwiec.payments.Topics
import com.dansiwiec.payments.models.Sku
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SkuRepo {

    var logger = LoggerFactory.getLogger(this::class.java)!!
    val skus = mutableMapOf<String, Sku>()

    fun lookup(sku: String): Sku? {
        return skus[sku]
    }

    @KafkaListener(id = "order-service-skus", topics = [Topics.SKUS])
    fun listenToSkus(sku: Sku) {
        logger.debug("Registering SKU ${sku.id}")
        skus[sku.id] = sku
    }

}
