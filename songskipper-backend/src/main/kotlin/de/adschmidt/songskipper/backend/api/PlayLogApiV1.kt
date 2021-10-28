package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.services.PlayLogService
import de.adschmidt.songskipper.backend.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/playlog/v1")
class PlayLogApiV1(
    private val userService: UserService,
    private val playLogService: PlayLogService
) : Loggable {

    @GetMapping("/recent-tracks")
    suspend fun getRecentTracks() : List<PlayLogTrack> {
        return playLogService.getRecentTracks(userService.verifyUserId(), 50)
    }

}