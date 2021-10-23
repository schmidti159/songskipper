package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.Track
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SkipperService(
    private val spotifyService: SpotifyService,
    private val userRepo: SpotifyUserRepo,
    private val applicationEventPublisher: ApplicationEventPublisher
) : Loggable {

    fun setSkipperState(userId : String, skipperActive : Boolean) {
        val user = userRepo.findById(userId)
            .orElseThrow { IllegalStateException("User with id '$userId' does not exist in DB!") }
        user.skipperActive = skipperActive
        userRepo.save(user)

    }

    fun skipTrack(userId: String, track: Track?): Boolean {
        return skipperActive(userId) && track != null && (
                track.title.contains("\\blive\\b".toRegex(RegexOption.IGNORE_CASE)) ||
                track.album.title.contains("\\blive\\b".toRegex(RegexOption.IGNORE_CASE)))
    }

    fun skipperActive(userId: String): Boolean {
        val user = userRepo.findById(userId)
            .orElseThrow { IllegalStateException("User with id '$userId' does not exist in DB!") }
        return user.skipperActive
    }


}