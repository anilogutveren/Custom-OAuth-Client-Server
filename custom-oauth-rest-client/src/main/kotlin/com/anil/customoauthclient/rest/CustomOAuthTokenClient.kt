package com.anil.customoauthclient.rest

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class CustomOAuthTokenClient(private val restTemplateBuilder: RestTemplateBuilder) {

    fun getResource(): String? {
        val restTemplate = restTemplateBuilder.build()

        val uri = UriComponentsBuilder.fromHttpUrl("http://localhost:8082").path("/resource").toUriString()

        val type = object : ParameterizedTypeReference<String>() {}
        val entity = HttpEntity<String>("abc")

        return restTemplate.exchange(
            uri,
            HttpMethod.GET,
            entity,
            type
        ).body
    }
}