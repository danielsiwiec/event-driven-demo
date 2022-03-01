package com.dansiwiec.kafkatesting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CatalogueService

fun main(args: Array<String>) {
    runApplication<CatalogueService>(*args)
}
