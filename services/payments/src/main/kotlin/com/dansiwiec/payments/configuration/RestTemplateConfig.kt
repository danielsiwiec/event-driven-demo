package com.dansiwiec.payments.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(@Autowired restTemplateBuilder: RestTemplateBuilder): RestTemplate? {
        return restTemplateBuilder.build()
    }
}