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
import java.text.DateFormat
import java.util.*


@SkipperTest
@kotlinx.coroutines.ExperimentalCoroutinesApi
internal class PlayLogApiIntegrationTest(
) {
    @Autowired
    private lateinit var playLogApi: PlayLogApiV1

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
    fun `getRecentTracks() returns the recent tracks returned from spotify stub`() {
        runTest {
            // arrange = use SpotifyServiceStub instead of SpotifyService
            val formatter = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT
            )

            // act
            val recentTracks = playLogApi.getRecentTracks()

            // assert
            assertThat(recentTracks).hasSize(50)
            assertThat(recentTracks[0].track.title).isEqualTo("name_0")
            assertThat(recentTracks[0].playedOn).isEqualTo(formatter.format(Date(0)))
            assertThat(recentTracks[42].track.title).isEqualTo("name_42")
            assertThat(recentTracks[42].playedOn).isEqualTo(formatter.format(Date(42)))
        }
    }

}