package com.dansiwiec.payments.services

import com.dansiwiec.payments.models.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TaxCalculatorTest {

    @BeforeEach
    fun init() {
        taxCalculator = TaxCalculator(customerService, skuService)
    }
    private lateinit var taxCalculator: TaxCalculator


    @Mock lateinit var customerService: CustomerService
    @Mock lateinit var skuService: SkuService

    @BeforeEach
    fun initMocks() {
        Mockito.lenient().`when`(customerService.lookup("1")).thenReturn(Customer(State.OR))
        Mockito.lenient().`when`(customerService.lookup("2")).thenReturn(Customer(State.CA))
        Mockito.lenient().`when`(skuService.lookup("1")).thenReturn(Sku(id = "1", name = "Lawnmower", price = 750.0))
    }

    @Test
    fun shouldReturnNoTaxForAStateWithNoTax() {
        val tax = taxCalculator.calculateTax(Order(items = listOf(LineItem(sku = "1", quantity = 1 )), customer = "1"))
        assertThat(tax).isEqualTo(0.0)
    }

    @Test
    fun shouldCalculateTaxForCalifornia() {
        val tax = taxCalculator.calculateTax(Order(items = listOf(LineItem(sku = "1", quantity = 1 )), customer = "2"))
        assertThat(tax).isEqualTo(54.37)
    }

    @Test
    fun shouldConsiderQuantity() {
        val tax = taxCalculator.calculateTax(Order(items = listOf(LineItem(sku = "1", quantity = 2 )), customer = "2"))
        assertThat(tax).isEqualTo(108.75)
    }

}