package com.dansiwiec.orders

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder


class Topics {

    companion object {
        const val ORDERS = "orders"
        const val SKUS = "skus"
        const val CUSTOMERS = "customers"
    }

    @Bean
    fun orderTopic(): NewTopic {
        return TopicBuilder.name(ORDERS)
            .partitions(1)
            .replicas(1)
            .build()
    }
}