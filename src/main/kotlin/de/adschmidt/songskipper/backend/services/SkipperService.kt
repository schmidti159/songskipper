package de.adschmidt.songskipper.backend.services

import com.wrapper.spotify.model_objects.specification.Track
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.CurrentTrackEvent
import de.adschmidt.songskipper.backend.events.SkipEvent
import de.adschmidt.songskipper.backend.logger
import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SkipperService(
    private val spotifyService: SpotifyService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : Loggable {

    private val tasksByUserId = mutableMapOf<String, SkipperTask>()

    @OptIn(DelicateCoroutinesApi::class)
    fun startSkipping(userId : String) {
        if(tasksByUserId.contains(userId)) {
            logger().info("Skipper for {} is already running.", userId)
            return
        }
        val task = SkipperTask(userId)
        tasksByUserId[userId] = task

        // start the skipper loop indefinitely (unless it is cancelled)
        GlobalScope.launch(Dispatchers.IO) {
            task.run()
        }
        logger().info("Started skipping for {}", userId)
    }

    fun stopSkipping(userId: String) {
        tasksByUserId.remove(userId)?.cancel()
        logger().info("Cancelled skipping for {}", userId)
    }

    fun skipTrack(track: Track?): Boolean {
        return track != null && (
                track.name.contains("live", ignoreCase = true) ||
                track.album.name.contains("live", ignoreCase = true))
    }

    inner class SkipperTask(private val userId: String) {

        private var cancelled = false

        suspend fun run() {
            while(!cancelled) {
                val sleepDuration = skipperLoop()
                delay(sleepDuration * 1000)
            }
        }

        fun cancel() {
            cancelled = true
        }

        /** run the skipper loop and return the seconds to sleep after this run */
        private suspend fun skipperLoop() : Long {
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
            if(skipTrack(track)) {
                applicationEventPublisher.publishEvent(SkipEvent(this, userId, track))
                spotifyService.skip(userId)
            }
            return if(remaining < 10) { 1 } else { 5 }
        }
    }
}