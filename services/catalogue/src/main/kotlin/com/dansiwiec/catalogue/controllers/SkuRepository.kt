package com.dansiwiec.catalogue.controllers

import com.dansiwiec.catalogue.Topics
import com.dansiwiec.catalogue.models.Sku
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class SkuRepository(val template: KafkaTemplate<String, Sku>) {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    companion object {
        val catalogue = mapOf(
            1 to Sku(id = 1, name = "Lonely Planet - Vietnam", category = "books", price = 19.99),
            2 to Sku(id = 2, name = "Yamaha Acoustic Guitar", category = "music instruments", price = 220.0),
            3 to Sku(id = 3, name = "Indoor HEPA Air Filter", category = "home appliances", price = 99.0),
            4 to Sku(id = 4, name = "Nintendo Switch", category = "game consoles", price = 299.0),
            5 to Sku(id = 5, name = "Roses bouquet", category = "plants", price = 25.0),
            6 to Sku(id = 6, name = "100% Orange Juice", category = "groceries", price = 3.99),
        )
    }

    @PostConstruct
    fun publishSkus() {
        logger.info("Publishing SKUs")
        catalogue.forEach { (id, sku) -> template.send(Topics.SKUS, id.toString(), sku)}
    }
}