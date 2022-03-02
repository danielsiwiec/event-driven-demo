package com.dansiwiec.catalogue

import com.dansiwiec.catalogue.models.Sku
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun testGetSku() {
        val response = restTemplate.getForEntity("/skus/1", Sku::class.java)
        assertThat(response.statusCode.value(), equalTo(200))
    }

    @Test
    fun testGetSkuNotFound() {
        val response = restTemplate.getForEntity("/skus/999", Void::class.java)
        assertThat(response.statusCode.value(), equalTo(404))
    }
}