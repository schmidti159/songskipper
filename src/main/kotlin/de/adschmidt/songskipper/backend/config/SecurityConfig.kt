package de.adschmidt.songskipper.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.security.crypto.password.PasswordEncoder




@EnableWebSecurity
class SecurityConfig {

    /**
     * Special config to allow access to h2 console in dev profile
     */
    @Configuration
    @Order(1)
    @Profile("dev")
    class H2ConsoleSecurityConfig : WebSecurityConfigurerAdapter() {
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
    class DefaultSecurityConfig : WebSecurityConfigurerAdapter() {
        @Bean
        fun encoder(): PasswordEncoder? {
            return BCryptPasswordEncoder()
        }

        override fun configure(http: HttpSecurity?) {
            http {
                formLogin {}
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
            }
        }
    }
}