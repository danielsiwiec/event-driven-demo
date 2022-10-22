package com.dansiwiec.payments

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder

class Topics {

    companion object {
        const val ORDERS = "orders"
        const val SKUS = "skus"
        const val CUSTOMERS = "customers"
        const val PAYMENTS = "payments"
    }

    @Bean
    fun paymentTopic(): NewTopic {
        return TopicBuilder.name(PAYMENTS)
            .partitions(1)
            .replicas(1)
            .build()
    }

}