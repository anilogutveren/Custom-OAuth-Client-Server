package com.anil.customoauthclient.controller

import com.anil.customoauthclient.rest.CustomOAuthTokenClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class WelcomeController(
    @Autowired
    private val customOAuthRestClient: CustomOAuthTokenClient
) {

    @GetMapping("/resource")
    fun welcome(): String {
        val welcome = customOAuthRestClient.getResource()
        println("Printing Fetched Response")
        return "<h1>$welcome</h1>"
    }

}