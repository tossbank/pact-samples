package com.tossbank.pactpocconsumer

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "server2-producer", hostInterface = "localhost", port = "1234", providerType = ProviderType.ASYNCH)
class MyKafkaConsumerTest {
    private val dateExample = LocalDate.now()

    // consume할 메시지에 대한 pact
    @Pact(provider = "server2-producer", consumer = "server1-consumer")
    fun pact(builder: MessagePactBuilder): MessagePact {
        val date = Date.from(dateExample.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        return builder
            .expectsToReceive("Dogs are born")
            .withContent(
                PactDslJsonBody()
                    .date("birthday", "yyyy-MM-dd", date)
                    .numberType("count", 100)
            )
            .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "pact")
    fun testPact(messages: List<Message>) {
        val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        val result = objectMapper.readValue<Born>(messages.first().contentsAsString())

        assertThat(result.birthday).isEqualTo(dateExample)
        assertThat(result.count).isEqualTo(100)
    }
}

data class Born(
    val birthday: LocalDate,
    val count: Long
)
