package com.dansiwiec.catalogue

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder


class Topics {

    companion object {
        const val SKUS = "skus"
    }

    @Bean
    fun orderTopic(): NewTopic {
        return TopicBuilder.name(SKUS)
            .partitions(1)
            .replicas(1)
            .build()
    }
}