package de.adschmidt.songskipper.backend.config

import de.adschmidt.songskipper.backend.spotify.SpotifyUserUpdateFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@EnableWebSecurity
class SecurityConfig {

    /**
     * Special config to allow access to h2 console in dev profile
     */
    @Configuration
    @Order(1)
    @Profile("dev")
    class H2ConsoleSecurityConfig {
        @Bean
        fun filterChain(http: HttpSecurity): SecurityFilterChain? {
            http {
                securityMatcher("/h2/**")
                authorizeRequests {
                    authorize(anyRequest, permitAll)
                }
                csrf {
                    disable()
                }
                headers {
                    frameOptions {
                        sameOrigin = true
                        deny = false
                    }
                }
            }
            return http.build()
        }
    }

    @Configuration
    class DefaultSecurityConfig(
        val spotifyUserUpdateFilter: SpotifyUserUpdateFilter
    ) {
        @Bean
        fun filterChain(http: HttpSecurity): SecurityFilterChain? {
            http {
                oauth2Login { }
                authorizeRequests {
                    authorize("/api/public/**", permitAll)
                    authorize("/api/**", authenticated)
                    authorize(anyRequest, permitAll)
                }
                csrf {
                    disable()
                }
                addFilterAfter<OAuth2LoginAuthenticationFilter>(spotifyUserUpdateFilter)
                cors {
                    disable()
                }
            }
            return http.build()
        }

        @Bean
        fun corsConfigurationSource(): CorsConfigurationSource? {
            val configuration = CorsConfiguration()
            configuration.allowedOrigins = listOf(CorsConfiguration.ALL)
            val source = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/**", configuration)
            return source
        }
    }
}