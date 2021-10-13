package de.adschmidt.songskipper.backend.config

import de.adschmidt.songskipper.backend.spotify.SpotifyUserUpdateFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@EnableWebSecurity
class SecurityConfig {

    /**
     * Special config to allow access to h2 console in dev profile
     */
    @Configuration
    @Order(1)
    @Profile("dev")
    class H2ConsoleSecurityConfig : WebSecurityConfigurerAdapter() {
//        override fun configure(web: WebSecurity) {
//            web.ignoring().antMatchers("/h2/**")
//        }
        override fun configure(http: HttpSecurity?) {
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
        }
    }

    @Configuration
    class DefaultSecurityConfig(
        val spotifyUserUpdateFilter : SpotifyUserUpdateFilter
    ) : WebSecurityConfigurerAdapter() {

        override fun configure(http: HttpSecurity?) {
            http {
                oauth2Login { }
                authorizeRequests {
                    authorize("/", permitAll)
                    authorize("/error", permitAll)
                    authorize("/webjars/**", permitAll)
                    authorize(anyRequest, authenticated)
                }
                addFilterAfter<OAuth2LoginAuthenticationFilter>(spotifyUserUpdateFilter)
            }
        }
    }
}