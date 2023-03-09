package com.anil.customoauthclient.config

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
class OAuthClientInterceptor : ClientHttpRequestInterceptor {

    private var manager: OAuth2AuthorizedClientManager
    private var principal: Authentication
    private var clientRegistration: ClientRegistration

    constructor(
        manager: OAuth2AuthorizedClientManager,
        clientRegistrationRepository: ClientRegistrationRepository
    ) {
        this.manager = manager
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
            override fun getAuthorities(): Collection<out GrantedAuthority> {
                return emptySet()
            }

            override fun getCredentials(): Any? {
                return null
            }

            override fun getDetails(): Any? {
                return null
            }

            override fun getPrincipal(): Any {
                return this
            }

            override fun isAuthenticated(): Boolean {
                return false
            }

            override fun setAuthenticated(isAuthenticated: Boolean) {
                // Do nothing
            }

            override fun getName(): String {
                return clientRegistration.clientId
            }
        }
    }


}