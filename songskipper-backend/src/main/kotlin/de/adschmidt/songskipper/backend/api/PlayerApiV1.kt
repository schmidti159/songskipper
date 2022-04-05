package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.services.SpotifyService
import de.adschmidt.songskipper.backend.services.TrackUpdaterService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/player/v1")
class PlayerApiV1(
    private val trackUpdaterService: TrackUpdaterService,
    private val userService: UserService,
    private val spotifyService: SpotifyService
) {
    @GetMapping("/currently-playing-track")
    suspend fun currentlyPlayingTrack(): CurrentlyPlayingState {
        return trackUpdaterService.getCurrentlyPlayingState(userService.verifyUserId())
    }
    @PostMapping("/next")
    suspend fun nextTrack(): CurrentlyPlayingState {
        val userId = userService.verifyUserId()
        spotifyService.nextTrack(userId)
        return trackUpdaterService.getCurrentlyPlayingState(userId)
    }
    @PostMapping("/previous")
    suspend fun previousTrack(): CurrentlyPlayingState {
        val userId = userService.verifyUserId()
        spotifyService.previousTrack(userId)
        return trackUpdaterService.getCurrentlyPlayingState(userId)
    }
    @PostMapping("/play")
    suspend fun play(): CurrentlyPlayingState {
        val userId = userService.verifyUserId()
        spotifyService.startPlayback(userId)
        return trackUpdaterService.getCurrentlyPlayingState(userId)
    }
    @PostMapping("/pause")
    suspend fun pause(): CurrentlyPlayingState {
        val userId = userService.verifyUserId()
        spotifyService.pausePlayback(userId)
        return trackUpdaterService.getCurrentlyPlayingState(userId)
    }
}