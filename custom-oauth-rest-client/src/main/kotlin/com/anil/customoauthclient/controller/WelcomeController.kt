package com.anil.customoauthclient.controller

import com.anil.customoauthclient.rest.CustomOAuthTokenClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class WelcomeController(private val customOAuthRestClient: CustomOAuthTokenClient) {

    @GetMapping("/resource")
    fun welcome(): String {
        val welcome = customOAuthRestClient.getResource()
        return "<h1>$welcome</h1>"
    }

}