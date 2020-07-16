package com.tossbank.pact.samples.server2

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DogsController {

	@GetMapping("/dogs")
	fun dogs(): List<Map<String, Int>> {
        return listOf(
            mapOf("dog" to 1),
			mapOf("dog" to 2)
		)
	}

	@GetMapping("/dogs/1")
	fun dogs1(): List<Map<String, Int>> {
		return listOf(
			mapOf("dog" to 1),
			mapOf("dog" to 2)
		)
	}
}
