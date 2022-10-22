package com.dansiwiec.payments.services

import com.dansiwiec.payments.models.PaymentRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PaymentGatewayService(
    @Autowired private val restTemplate: RestTemplate,
    @Value("\${paymentGatewayUrl}") private val paymentGatewayUrl: String,
    @Autowired private val customerService: CustomerService
) {

    fun submitPayment(customerId: String, total: Double) {
        val accountNumber = customerService.lookup(customerId)?.accountNumber ?: error("Customer missing")
        restTemplate.postForEntity("$paymentGatewayUrl/api/payment", PaymentRequest(accountNumber, total), Void::class.java)
    }
}