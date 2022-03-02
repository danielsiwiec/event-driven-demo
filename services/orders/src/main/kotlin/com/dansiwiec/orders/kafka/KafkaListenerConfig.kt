package com.dansiwiec.orders.kafka

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter

@Configuration
class KafkaListenerConfig {

    @Bean
    fun messageConverter(): RecordMessageConverter {
        return StringJsonMessageConverter()
    }
}