package com.tossbank.pact.samples.server2

import au.com.dius.pact.provider.PactVerifyProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.LocalDate

class KafkaMessageSpecification {

    // 이 producer가 발송하는 kakfa message의 샘플
    @PactVerifyProvider("Dogs are born")
    fun born(): String {
        val objectMapper = ObjectMapper()
            .registerKotlinModule()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModule(JavaTimeModule())

        return objectMapper
            .writeValueAsString(
                Born(
                    birthday = LocalDate.now(),
                    count = 100
                )
            )
    }
}

data class Born(
    val birthday: LocalDate,
    val count: Long
)
