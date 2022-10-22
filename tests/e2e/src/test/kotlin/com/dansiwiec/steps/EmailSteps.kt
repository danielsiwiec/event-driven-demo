package com.dansiwiec.steps

import io.cucumber.java8.En
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await

class EmailSteps : En {

    init {

        Before { _ ->
            RestAssured.requestSpecification = RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build()
        }

        Before { _ ->
            RestAssured.given()
                .post("http://localhost:8082/sentEmailCount/reset")
                .then()
                .statusCode(200)
        }

        Then("an email should be sent out") {
            await().until {
                RestAssured.given()
                    .get("http://localhost:8082/sentEmailCount")
                    .body.`as`(Int::class.java)
                    .equals(1)
            }
        }
    }
}