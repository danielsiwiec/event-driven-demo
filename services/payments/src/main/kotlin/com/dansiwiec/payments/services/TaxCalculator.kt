package com.dansiwiec.payments.services

import com.dansiwiec.payments.models.Order
import com.dansiwiec.payments.models.State
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import kotlin.math.pow
import kotlin.math.round

    @Component
    class TaxCalculator(@Autowired private val customerRepo: CustomerRepo, @Autowired private val skuRepo: SkuRepo) {

        fun calculateTax(order: Order): Double {
            if (order.items.isEmpty()) return 0.0
            val customer = customerRepo.lookup(order.customer)
            val taxRate = stateTaxRate(customer?.state ?: error("Customer missing"))

            return order.items
                .sumOf { it.quantity * taxRate * (skuRepo.lookup(it.sku)?.price ?: error("SKU missing")) }
                .round(2)
        }

        private fun stateTaxRate(state: State): Double {
            return when (state) {
                State.CA -> 0.0725
                State.ME -> 0.055
                State.MN -> 0.0
                State.OR -> 0.0
                else -> throw IllegalArgumentException("No tax record for state $state")
            }
        }
    }

private fun Double.round(places: Int): Double {
    return round(this * 10.0.pow(places)) / 10.0.pow(places)
}