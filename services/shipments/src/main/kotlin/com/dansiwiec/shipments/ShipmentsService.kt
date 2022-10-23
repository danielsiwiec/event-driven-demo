package com.dansiwiec.shipments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class EmailService

fun main(args: Array<String>) {
    runApplication<EmailService>(*args)
}
