package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.events.CurrentlyPlayingState
import de.adschmidt.songskipper.backend.services.CurrentTrackUpdaterService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/currently-playing/v1")
class CurrentlyPlayingApiV1(
    private val currentTrackUpdaterService: CurrentTrackUpdaterService,
    private val userService: UserService
) {
    @GetMapping("/currently-playing-track")
    suspend fun startSkipping(): CurrentlyPlayingState {
        return currentTrackUpdaterService.getCurrentlyPlayingState(userService.verifyUserId())
    }
}