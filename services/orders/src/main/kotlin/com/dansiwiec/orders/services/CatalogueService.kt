package com.dansiwiec.orders.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class CatalogueService(@Value("\${catalogueService.url}") val url: String, val restTemplate: RestTemplate) {

    var logger = LoggerFactory.getLogger(this::class.java)!!

    fun isValid(sku: Int): Boolean {
        try {
            restTemplate.getForObject("$url/skus/$sku", Void::class.java)
        }
        catch (e: HttpClientErrorException.NotFound) {
            logger.warn("SKU $sku does not exist")
            return false
        }
        logger.debug("SKU $sku found")
        return true
    }

}
