package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SkipperService(
    private val spotifyService: SpotifyService,
    private val userRepo: SpotifyUserRepo,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val ruleService: RuleService
) : Loggable {

    fun setSkipperState(userId : String, skipperActive : Boolean) {
        val user = userRepo.findById(userId)
            .orElseThrow { IllegalStateException("User with id '$userId' does not exist in DB!") }
        user.skipperActive = skipperActive
        userRepo.save(user)

    }

    fun skipTrack(userId: String, track: Track?): Boolean {
        if (track == null) {
            return false
        }
        return ruleService.findMatchingRules(userId, track).isNotEmpty()
    }

    fun skipperActive(userId: String): Boolean {
        val user = userRepo.findById(userId)
            .orElseThrow { IllegalStateException("User with id '$userId' does not exist in DB!") }
        return user.skipperActive
    }


}