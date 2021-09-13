package de.adschmidt.songskipper.backend.api.v1

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.services.SpotifySkipperService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/api/v1/")
class SongSkipperAPIV1(
    private val skipperService: SpotifySkipperService
) : Loggable {

    @GetMapping("/start-skipping") // TODO this is not GET
    suspend fun startSkipping() {
        skipperService.startSkipping(getCurrentUserId())
    }
    private fun getCurrentUserId() : String {
        val principal = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("buildSpotifyApi() must only be called when a user is logged in!")
        return principal.name
    }
}