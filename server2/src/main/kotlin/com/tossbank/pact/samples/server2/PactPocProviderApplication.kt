package com.tossbank.pact.samples.server2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PactPocProviderApplication

fun main(args: Array<String>) {
	runApplication<PactPocProviderApplication>(*args)
}
