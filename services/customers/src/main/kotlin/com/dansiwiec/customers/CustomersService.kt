package com.dansiwiec.customers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CustomersService

fun main(args: Array<String>) {
    runApplication<CustomersService>(*args)
}
