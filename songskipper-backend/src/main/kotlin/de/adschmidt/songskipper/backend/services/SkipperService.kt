package de.adschmidt.songskipper.backend.services

import com.wrapper.spotify.model_objects.specification.Track
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.CurrentTrackEvent
import de.adschmidt.songskipper.backend.events.SkipEvent
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SkipperService(
    private val spotifyService: SpotifyService,
    private val userRepo: SpotifyUserRepo,
    private val applicationEventPublisher: ApplicationEventPublisher
) : Loggable {

    private val tasksByUserId = mutableMapOf<String, CurrentSongUpdaterTask>()

    fun setSkipperState(userId : String, skipperActive : Boolean) {
        val user = userRepo.findById(userId)
            .orElseThrow { IllegalStateException("User with id '$userId' does not exist in DB!") }
        user.skipperActive = skipperActive
        userRepo.save(user)

    }

    @OptIn(DelicateCoroutinesApi::class)
    @EventListener
    fun userIdChanged(event: UserChangedEvent) {
        val userId = event.userId
        if(userId == null) {
            logger().error("UserChangedEvent had no user.id")
            return
        }
        if(tasksByUserId.contains(userId)) {
            logger().debug("CurrentSongUpdaterTask for {} is already running.", userId)
            return
        }
        val task = CurrentSongUpdaterTask(userId)
        tasksByUserId[userId] = task

        // start the skipper loop indefinitely (unless it is cancelled)
        GlobalScope.launch(Dispatchers.IO) {
            task.run()
        }
        logger().info("Started CurrentSongUpdaterTask for {}", userId)
    }


    fun skipTrack(track: Track?): Boolean {
        return track != null && (
                track.name.contains("\\blive\\b".toRegex(RegexOption.IGNORE_CASE)) ||
                track.album.name.contains("\\blive\\b".toRegex(RegexOption.IGNORE_CASE)))
    }

    fun skipperActive(userId: String): Boolean {
        val user = userRepo.findById(userId)
            .orElseThrow { IllegalStateException("User with id '$userId' does not exist in DB!") }
        return user.skipperActive
    }

    inner class CurrentSongUpdaterTask(private val userId: String) {

        private var cancelled = false

        suspend fun run() {
            while(!cancelled) {
                val sleepDuration = currentSongUpdaterLoop()
                delay(sleepDuration * 1000)
            }
        }

        fun cancel() {
            cancelled = true
        }

        /** run the skipper loop and return the seconds to sleep after this run */
        private suspend fun currentSongUpdaterLoop() : Long {
            val currentlyPlaying = spotifyService.getCurrentlyPlayingTrack(userId)
            if(currentlyPlaying == null || currentlyPlaying.item == null) {
                applicationEventPublisher.publishEvent(CurrentTrackEvent(this, userId,null, false,0))
                return 30
            }

            val remaining = (currentlyPlaying.item.durationMs - currentlyPlaying.progress_ms) / 1000
            val track = spotifyService.track(currentlyPlaying.item.id, userId)
            applicationEventPublisher.publishEvent(CurrentTrackEvent(this, userId,
                track, currentlyPlaying.is_playing, currentlyPlaying.progress_ms))
            if(!currentlyPlaying.is_playing) {
                return 10
            }
            if(skipperActive(userId) && skipTrack(track)) {
                applicationEventPublisher.publishEvent(SkipEvent(this, userId, track))
                spotifyService.skip(userId)
                return 1
            }
            return if(remaining < 10) { 1 } else { 5 }
        }
    }
}