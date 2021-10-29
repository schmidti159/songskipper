package de.adschmidt.songskipper.backend

import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.SpotifyService
import de.adschmidt.songskipper.backend.services.SpotifyServiceStub
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.test.context.ActiveProfiles

@TestConfiguration
@ComponentScan
@Order
class TestConfig {
    companion object {
        const val USER_ID = "user"
    }

    @Bean
    @Primary
    fun spotifyServiceStub(spotifyUserRepo: SpotifyUserRepo): SpotifyService {
        return SpotifyServiceStub(spotifyUserRepo)
    }
}

@SpringBootTest
@Import(TestConfig::class)
@ActiveProfiles("test")
annotation class SkipperTest {

}