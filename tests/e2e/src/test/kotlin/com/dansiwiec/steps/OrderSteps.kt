package com.dansiwiec.steps

import com.dansiwiec.models.LineItem
import com.dansiwiec.models.Order
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.response.Response


class OrderSteps : En {

    private var createOrderResponse: Response? = null

    init {

        DataTableType { entry: Map<String, String> ->
            LineItem(entry["sku"]!!.toInt(), entry["quantity"]!!.toInt())
        }

        Before { _: Scenario ->
            RestAssured.requestSpecification = RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build()
            RestAssured.baseURI = "http://localhost:8080"
        }

        When("I create a following order for customer {int}:") { customerId: Int, lineItems: DataTable ->
            val order = Order(lineItems.asList(LineItem::class.java), customerId)
            createOrderResponse = given().body(order).post("/orders")

        }

        Then("the response should be {int}") { statusCode:Int ->
            createOrderResponse?.then()?.statusCode(statusCode)
        }
    }
}