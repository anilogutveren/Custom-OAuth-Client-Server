package com.anil.customoauthclient

import com.anil.customoauthclient.config.OAuth2ClientSecurityConfig
import com.anil.customoauthclient.config.OAuthClientInterceptor
import com.anil.customoauthclient.rest.CustomOAuthTokenClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInitializer
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.ResponseActions
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import java.time.Instant

@RestClientTest
@Import(OAuth2ClientSecurityConfig::class, CustomOAuthTokenClient::class)
@AutoConfigureMockRestServiceServer
@AutoConfigureWebClient(registerRestTemplate = true)
class CustomoauthApplicationMockTests {

    @Mock
    var mockRestTemplateBuilder = RestTemplateBuilder(MockServerRestTemplateCustomizer())

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var customOAuthTokenClient: CustomOAuthTokenClient

    @Autowired
    private lateinit var server: MockRestServiceServer

    @Autowired
    private lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Autowired
    private lateinit var restTemplateBuilderConfigured: RestTemplateBuilder

    @MockBean
    private lateinit var manager: OAuth2AuthorizedClientManager

    @TestConfiguration
    class TestConfig {
        @Bean
        fun clientRegistrationRepository(): ClientRegistrationRepository {
            return InMemoryClientRegistrationRepository(
                ClientRegistration
                    .withRegistrationId("springauth")
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .clientId("test")
                    .tokenUri("test")
                    .build()
            )
        }

        @Bean
        fun auth2AuthorizedClientService(clientRegistrationRepository: ClientRegistrationRepository?): OAuth2AuthorizedClientService {
            return InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
        }

        @Bean
        fun oAuthClientInterceptor(
            manager: OAuth2AuthorizedClientManager,
            clientRegistrationRepository: ClientRegistrationRepository
        ): OAuthClientInterceptor {
            return OAuthClientInterceptor(manager, clientRegistrationRepository)
        }
    }

    @BeforeEach
    fun setUp() {
        val clientRegistration = clientRegistrationRepository
            .findByRegistrationId("springauth")
        val token = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "test", Instant.MIN, Instant.MAX
        )
        Mockito.`when`(manager.authorize(ArgumentMatchers.any())).thenReturn(
            OAuth2AuthorizedClient(
                clientRegistration,
                "test", token
            )
        )
        val restTemplate = restTemplateBuilderConfigured.build()
        server = MockRestServiceServer.bindTo(restTemplate).build()
        Mockito.`when`(mockRestTemplateBuilder.build()).thenReturn(restTemplate)
        customOAuthTokenClient = CustomOAuthTokenClient(mockRestTemplateBuilder)
    }

    @Test
    fun getWelcomeTest() {

        restTemplate.clientHttpRequestInitializers.add(
            ClientHttpRequestInitializer { request ->
                request.headers.add(
                    "Authorization",
                    "Bearer JWT"
                )
            }
        )

        val responseActions: ResponseActions = server.expect(
            MockRestRequestMatchers.requestTo(
                "http://localhost:8082/resource"
            )
        ).andExpect(MockRestRequestMatchers.header("Authorization", "Bearer test"))


        responseActions.andRespond(MockRestResponseCreators.withSuccess("Welcome", MediaType.APPLICATION_JSON))

        val response = customOAuthTokenClient.getResource()
        Assertions.assertThat(response).isNotEmpty

    }

}
