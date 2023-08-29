package com.anil.customoauthclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CustomOAuthApplication

fun main(args: Array<String>) {
	runApplication<CustomOAuthApplication>(*args)
}
