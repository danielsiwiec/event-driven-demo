package com.dansiwiec.payments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class PaymentsService

fun main(args: Array<String>) {
    runApplication<PaymentsService>(*args)
}
