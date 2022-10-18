import io.gatling.javaapi.core.*

import java.time.Duration

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*

class BasicSimulation : Simulation() {

    private val httpProtocol = http.baseUrl("http://localhost:8080/") // Here is the root for all relative URLs
        .contentTypeHeader("application/json")
        .acceptHeader("application/json") // Here are the common headers

    private val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
        .exec(
            http("request_1").post("/orders").body(StringBody(
                """
                    {
                        "items": [{"sku":"1", "quantity": 1}],
                        "customerId": "1"
                     }
                """.trimIndent()
            ))
        )

    init {
        setUp(
            scn.injectOpen(
                constantUsersPerSec(10.0).during(Duration.ofSeconds(30))
            ).protocols(httpProtocol)
        )
    }
}