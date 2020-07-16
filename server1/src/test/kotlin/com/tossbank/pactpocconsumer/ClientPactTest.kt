package com.tossbank.pactpocconsumer

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import io.restassured.RestAssured
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "server2", hostInterface = "localhost", port = "1234")
class ClientPactTest {
    private val dateTime: LocalDateTime = LocalDateTime.now()
    private val dateResult: String = "2013-08-16T15:31:20+10:00"

    // This defines the expected interaction for out test
    @Pact(provider = "server2", consumer = "server1")
    fun pact(builder: PactDslWithProvider): RequestResponsePact {
        return builder
            .given("data count > 0") // provider가 state change api를 제공해야한다
            .uponReceiving("a request for json data")
            .path("/provider.json")
            .method("GET")
            .query("validDate=$dateTime")
            .willRespondWith()
            .status(200)
            .body(
                PactDslJsonBody()
                    .stringValue("test", "NO")
                    .stringValue("date", dateResult)
                    .numberValue("count", 100)
            )
            .toPact()
    }

    @Test
    fun pactWithOurProvider(mockServer: MockServer) {
        RestAssured
            .given()
            .baseUri(mockServer.getUrl() + "/provider.json")
            .`when`()
            .queryParam("validDate", dateTime.toString())
            .get()
            .then()
            .body("test", Matchers.`is`("NO"))
            .body("date", Matchers.`is`(dateResult))
            .body("count", Matchers.`is`(100))
            .statusCode(HttpStatus.OK.value())
    }
}
