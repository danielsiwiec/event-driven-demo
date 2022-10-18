package scenarios

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.http.HttpDsl

class CreateOrder {

    companion object {

        val requestName = "POST /orders"

        val createOrder = CoreDsl.scenario("Scenario Name") // A scenario is a chain of requests and pauses
            .exec(
                HttpDsl.http(requestName).post("/orders").body(
                    CoreDsl.StringBody(
                        """
                    {
                        "items": [{"sku":"1", "quantity": 1}],
                        "customerId": "1"
                     }
                """.trimIndent()
                    )
                )
            )
    }
}