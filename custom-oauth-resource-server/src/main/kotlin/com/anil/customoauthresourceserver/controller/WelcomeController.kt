package com.anil.customoauthresourceserver.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WelcomeController {

    @GetMapping("/resource")
    fun welcome(): String {
        println("Welcome called")
        return "<h1>Welcome</h1>"
    }

}