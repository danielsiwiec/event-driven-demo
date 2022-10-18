package com.dansiwiec.customers

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder


class Topics {

    companion object {
        const val CUSTOMERS = "customers"
    }

    @Bean
    fun orderTopic(): NewTopic {
        return TopicBuilder.name(CUSTOMERS)
            .partitions(1)
            .replicas(1)
            .build()
    }
}