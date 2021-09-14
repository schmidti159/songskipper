package de.adschmidt.songskipper.backend.api.v1

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.services.SkipperService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/v1/")
class SongSkipperAPIV1(
    private val skipperService: SkipperService,
    private val spotifyUserRepo: SpotifyUserRepo
) : Loggable {

    @GetMapping("/skipper/start") // TODO move (use just for testing)
    suspend fun startSkipping() {
        skipperService.startSkipping(getCurrentUserId())
    }
    @GetMapping("/skipper/stop") // TODO move (use just for testing)
    suspend fun stopSkipping() {
        skipperService.stopSkipping(getCurrentUserId())
    }

    private fun getCurrentUserId() : String {
        val principal = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("api must only be called when a user is logged in!")
        return principal.name
    }
}