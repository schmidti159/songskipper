package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.SkipperTest
import de.adschmidt.songskipper.backend.TestConfig.Companion.USER_ID
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUserModel
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.UserService
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean


@SkipperTest
@kotlinx.coroutines.ExperimentalCoroutinesApi
internal class CurrentylPlayingApiIntegrationTest(
) {

    @Autowired
    private lateinit var currentlyPlayingApi: PlayerApiV1

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
    fun `currentlyPlayingTrack() returns current Track from spotify stub`() {
        runTest {
            // arrange = use SpotifyServiceStub instead of SpotifyService

            // act
            val currentlyPlaying = currentlyPlayingApi.currentlyPlayingTrack()

            // assert
            assertThat(currentlyPlaying.track?.title).isEqualTo("name_current_track_id")
            assertThat(currentlyPlaying.track?.durationMs).isEqualTo(42)
            assertThat(currentlyPlaying.progressMs).isEqualTo(23)
            assertThat(currentlyPlaying.isPaused).isFalse()
        }
    }

}