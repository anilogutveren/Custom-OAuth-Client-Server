package com.anil.customoauthclient.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component

@Component
@Qualifier("custominterceptor")
class OAuthClientInterceptor(
    private var manager: OAuth2AuthorizedClientManager,
    clientRegistrationRepository: ClientRegistrationRepository
) : ClientHttpRequestInterceptor {

    private lateinit var principal: Authentication
    private lateinit var clientRegistration: ClientRegistration

    init {
        this.principal = createPrincipal()
        this.clientRegistration = clientRegistrationRepository.findByRegistrationId("springauth")
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
            .withClientRegistrationId(clientRegistration.registrationId)
            .principal(createPrincipal())
            .build()

        val client = manager.authorize(oAuth2AuthorizeRequest)

        if (client != null) {
            request.headers.add(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + client.accessToken.tokenValue
            )
            println("AccessToken is fetched:" + client.accessToken.tokenValue)
        } else {
            throw IllegalStateException("Missing credentials");
        }

        return execution.execute(request, body);

    }

    private fun createPrincipal(): Authentication {
        return object : Authentication {
            override fun getName(): String {
                TODO("Not yet implemented")
            }

            override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
                TODO("Not yet implemented")
            }

            override fun getCredentials(): Any {
                TODO("Not yet implemented")
            }

            override fun getDetails(): Any {
                TODO("Not yet implemented")
            }

            override fun getPrincipal(): Any {
                TODO("Not yet implemented")
            }

            override fun isAuthenticated(): Boolean {
                TODO("Not yet implemented")
            }

            override fun setAuthenticated(isAuthenticated: Boolean) {
                TODO("Not yet implemented")
            }
        }
    }
}