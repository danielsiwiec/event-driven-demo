package com.dansiwiec.steps

import io.cucumber.java8.En
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`

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

            RestAssured.given()
                .get("http://localhost:8082/sentEmailCount")
                .then()
                .body(`is`(0.toString()))
        }

        Then("an email should be sent out") {
            RestAssured.given()
                .get("http://localhost:8082/sentEmailCount")
                .then()
                .body(`is`(1.toString()))
        }
    }
}