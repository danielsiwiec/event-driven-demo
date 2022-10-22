package com.dansiwiec.payments.services

import com.dansiwiec.payments.models.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PricingService(
    @Autowired private val skuService: SkuService,
    @Autowired private val taxCalculator: TaxCalculator
) {

    fun calculatePrice(order: Order): Double {
        return order.items
            .sumOf { item -> skuService.lookup(item.sku)?.price?.times(item.quantity) ?: error("SKU missing") }
            .plus(taxCalculator.calculateTax(order))
    }
}