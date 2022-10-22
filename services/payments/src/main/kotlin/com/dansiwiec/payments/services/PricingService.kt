package com.dansiwiec.payments.services

import com.dansiwiec.payments.models.Order
import com.dansiwiec.payments.repos.SkuRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PricingService(
    @Autowired private val skuRepo: SkuRepo,
    @Autowired private val taxCalculator: TaxCalculator
) {

    fun calculatePrice(order: Order): Double {
        return order.items
            .sumOf { item -> skuRepo.lookup(item.sku)?.price?.times(item.quantity) ?: error("SKU missing") }
            .plus(taxCalculator.calculateTax(order))
    }
}