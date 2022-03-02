package com.dansiwiec.orders.health

import com.dansiwiec.orders.repository.SkusRepository
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class SkuHealthIndicator(val skusRepository: SkusRepository) : HealthIndicator {

    override fun health(): Health {
        return if (skusRepository.skus.isNotEmpty()) Health.up().build() else Health.down().build()
    }
}