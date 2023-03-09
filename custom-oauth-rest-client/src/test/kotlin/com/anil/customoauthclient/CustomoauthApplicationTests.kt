package com.anil.customoauthclient

import com.anil.customoauthclient.rest.CustomOAuthTokenClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CustomoauthApplicationTests {

	@Autowired
	private lateinit var customOAuthTokenClient: CustomOAuthTokenClient

	@Test
	fun getWelcomeTest() {
		val response = customOAuthTokenClient.getResource()
		println(response)
	}
}
