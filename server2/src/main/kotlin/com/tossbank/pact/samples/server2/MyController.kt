package com.tossbank.pact.samples.server2

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MyController {

    @GetMapping("/provider.json")
	fun provider(): Provider {
		return Provider(
			date = "2013-08-16T15:31:20+10:00",
			test = "NO",
			count = 100
		)
	}
}

