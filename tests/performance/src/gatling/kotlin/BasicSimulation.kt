import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration

    class BasicSimulation : Simulation() {
        init {
            setUp(
                scenario("order happy path")
                    .group("Process Order").on(
                        exec(resetCounter())
                            .exec(postOrder())
                            .exec(waitForPaymentToProcess())
                    ).injectClosed(constantConcurrentUsers(1).during(Duration.ofSeconds(30)))
            ).protocols(
                http
                    .contentTypeHeader("application/json")
                    .acceptHeader("application/json")
            )
        }
    }

fun postOrder() = http("create order")
    .post("http://localhost:8080/orders").body(
        StringBody(
            """
                    {
                        "items": [{"sku":"1", "quantity": 1}],
                        "customerId": "1"
                     }
                """.trimIndent()
        )
    ).check(status().`is`(200))

fun resetCounter() = http("reset payment counter").post("http://localhost:8081/sentPaymentsCount/reset")

fun waitForPaymentToProcess() = doWhile { !it.getString("count").equals("1") }.on(
    exec(
        http("get payment counter")
            .get("http://localhost:8081/sentPaymentsCount")
            .check(bodyString().saveAs("count"))
    )
)