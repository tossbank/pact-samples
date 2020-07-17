package com.tossbank.pactpocconsumer

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "server2", hostInterface = "localhost", port = "1234")
class MyClientApplicationTest {
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

    // pacts에서 제시된 요청이 실제로 반드시 실행되어야한다. (이유는 모르겠는데 쓰지 않을 pacts를 만들지 않게 하기 위함일까?)
    @Test
    fun pactWithServer2(mockServer: MockServer) {
        val result = MyClientApplication(mockServer.getUrl() + "/provider.json").get(dateTime)

        assertThat(result.test).isEqualTo("NO")
        assertThat(result.date).isEqualTo(dateResult)
        assertThat(result.count).isEqualTo(100)
    }
}
