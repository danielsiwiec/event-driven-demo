package com.dansiwiec.steps

import io.cucumber.java8.En
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import java.time.Duration

class ShipmentsSteps : En {

    init {

        Before { _ ->
            RestAssured.requestSpecification = RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build()
        }

        Before { _ ->
            RestAssured.given()
                .post("http://localhost:8084/shipmentsCount/reset")
                .then()
                .statusCode(200)
        }

        Then("{int} shipment(s) should be dispatched") { count:Int ->
            await().until {
                RestAssured.given()
                    .get("http://localhost:8084/shipmentsCount")
                    .body.`as`(Int::class.java)
                    .equals(count)
            }
        }

        Then("a shipment should not be dispatched") {
            await().during(Duration.ofSeconds(2)).until {
                RestAssured.given()
                    .get("http://localhost:8084/shipmentsCount")
                    .body.`as`(Int::class.java)
                    .equals(0)
            }
        }
    }
}