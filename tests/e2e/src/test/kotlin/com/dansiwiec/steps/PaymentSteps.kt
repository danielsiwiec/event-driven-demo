package com.dansiwiec.steps

import io.cucumber.java8.En
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import java.time.Duration

class PaymentSteps : En {

    init {

        Before { _ ->
            RestAssured.requestSpecification = RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build()
        }

        Before { _ ->
            RestAssured.given()
                .post("http://localhost:8081/sentPaymentsCount/reset")
                .then()
                .statusCode(200)
        }

        Then("a payment should be sent out") {
            await().until {
                RestAssured.given()
                    .get("http://localhost:8081/sentPaymentsCount")
                    .body.`as`(Int::class.java)
                    .equals(1)
            }
        }

        Then("a payment should not be sent out") {
            await().during(Duration.ofSeconds(2)).until {
                RestAssured.given()
                    .get("http://localhost:8081/sentPaymentsCount")
                    .body.`as`(Int::class.java)
                    .equals(0)
            }
        }
    }
}