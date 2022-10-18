import io.gatling.javaapi.core.*

import java.time.Duration

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import scenarios.CreateOrder.Companion.requestName
import scenarios.CreateOrder.Companion.createOrder

class BasicSimulation : Simulation() {

    private val httpProtocol = http.baseUrl("http://localhost:8080/") // Here is the root for all relative URLs
        .contentTypeHeader("application/json")
        .acceptHeader("application/json") // Here are the common headers

    init {
        setUp(
            createOrder.injectOpen(
                constantUsersPerSec(10.0).during(Duration.ofSeconds(30))
            ).protocols(httpProtocol)
        ).assertions(
            global().successfulRequests().percent().shouldBe(100.0),
            details(requestName).responseTime().percentile(75.0).lt(Duration.ofMillis(20).toMillis().toInt())
        )
    }
}