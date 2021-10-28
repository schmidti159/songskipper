package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.api.CurrentlyPlayingState
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.events.CurrentTrackEvent
import de.adschmidt.songskipper.backend.events.SkipEvent
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.logger
import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CurrentTrackUpdaterService(
    private val spotifyService: SpotifyService,
    private val skipperService: SkipperService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : Loggable{

    private val tasksByUserId = mutableMapOf<String, CurrentTrackUpdaterTask>()

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
        val task = CurrentTrackUpdaterTask(userId)
        tasksByUserId[userId] = task

        // start the skipper loop indefinitely (unless it is cancelled)
        GlobalScope.launch(Dispatchers.IO) {
            task.run()
        }
        logger().info("Started CurrentSongUpdaterTask for {}", userId)
    }

    suspend fun getCurrentlyPlayingState(userId: String): CurrentlyPlayingState {
        val currentlyPlaying = spotifyService.getCurrentlyPlayingTrack(userId)
        if(currentlyPlaying == null || currentlyPlaying.item == null) {
            return CurrentlyPlayingState(null, 0, false)
        }
        val spotifyTrack = spotifyService.track(currentlyPlaying.item.id, userId)
        val apiTrack = if(spotifyTrack != null) Track(spotifyTrack) else null
        return CurrentlyPlayingState(apiTrack, currentlyPlaying.progress_ms, !currentlyPlaying.is_playing)
    }

    inner class CurrentTrackUpdaterTask(private val userId: String) {

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
            val playingState = getCurrentlyPlayingState(userId)
            applicationEventPublisher.publishEvent(CurrentTrackEvent(this, userId, playingState))
            if(playingState.track == null) {
                return 30
            }
            if(playingState.isPaused) {
                return 10
            }
            if(skipperService.skipTrack(userId, playingState.track)) {
                applicationEventPublisher.publishEvent(SkipEvent(this, userId, playingState.track))
                spotifyService.skip(userId)
                return 1
            }
            val remaining = (playingState.track.durationMs - playingState.progressMs) / 1000
            return if(remaining < 10) { 1 } else { 5 }
        }
    }
}