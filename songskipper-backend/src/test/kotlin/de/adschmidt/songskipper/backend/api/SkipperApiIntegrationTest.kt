package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.SkipperTest
import de.adschmidt.songskipper.backend.TestConfig.Companion.USER_ID
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUserModel
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.UserService
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean


@SkipperTest
@kotlinx.coroutines.ExperimentalCoroutinesApi
internal class SkipperApiIntegrationTest(
) {
    @Autowired
    private lateinit var skipperApi: SkipperApiV1

    @Autowired
    private lateinit var spotifyUserRepo: SpotifyUserRepo

    @MockBean
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        spotifyUserRepo.save(SpotifyUserModel(USER_ID))
        `when`(userService.verifyUserId()).thenReturn(USER_ID)
    }

    @AfterEach
    fun cleanUp() {
        spotifyUserRepo.deleteAll()
    }

    @Test
    fun `skipper can be stopped and started`() {
        runBlockingTest {
            // default: Skipping is active
            assertThat(skipperApi.active()).isTrue
            // no change
            skipperApi.startSkipping()
            assertThat(skipperApi.active()).isTrue
            // stop
            skipperApi.stopSkipping()
            assertThat(skipperApi.active()).isFalse
            // no change
            skipperApi.stopSkipping()
            assertThat(skipperApi.active()).isFalse
            // start
            skipperApi.startSkipping()
            assertThat(skipperApi.active()).isTrue
        }
    }
}