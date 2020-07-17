package com.tossbank.pactpocconsumer

import com.google.gson.Gson
import org.springframework.web.util.UriComponentsBuilder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime

class MyClientApplication(private val apiUri: String) {

    fun get(dateTime: LocalDateTime): Foo {
        val client = HttpClient.newHttpClient()
        val uri = UriComponentsBuilder.fromUriString(apiUri).queryParam("validDate", dateTime.toString()).build().toUri()
        val httpRequest = HttpRequest.newBuilder().uri(uri).GET().build()
        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return Gson().fromJson(response.body(), Foo::class.java)
    }
}

data class Foo(
    val test: String,
    val date: String,
    val count: Int
)
