package com.dansiwiec.payments.services

import com.dansiwiec.payments.models.PaymentRequest
import com.dansiwiec.payments.repos.CustomerRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@Component
@RestController
@RequestMapping("/sentPaymentsCount")
class PaymentGatewayService(
    @Autowired private val restTemplate: RestTemplate,
    @Value("\${paymentgateway.url}") private val paymentGatewayUrl: String,
    @Autowired private val customerRepo: CustomerRepo
) {

    var paymentsCount = 0

    fun submitPayment(customerId: String, total: Double) {
        val accountNumber = customerRepo.lookup(customerId)?.accountNumber ?: error("Customer missing")
        restTemplate.postForEntity("$paymentGatewayUrl/api/payment", PaymentRequest(accountNumber, total), Void::class.java)
        paymentsCount++
    }

    @GetMapping
    fun count():Int {
        return paymentsCount
    }

    @PostMapping("reset")
    fun reset() {
        paymentsCount = 0
    }
}