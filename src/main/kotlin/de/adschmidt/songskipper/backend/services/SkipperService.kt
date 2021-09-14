package de.adschmidt.songskipper.backend.services

import com.wrapper.spotify.model_objects.IPlaylistItem
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import kotlinx.coroutines.*
import org.springframework.stereotype.Component

@Component
class SkipperService(
    private val spotifyService: SpotifyService
) : Loggable {

    private val tasksByUserId = mutableMapOf<String, SkipperTask>()

    @OptIn(DelicateCoroutinesApi::class)
    fun startSkipping(userId : String) {
        if(tasksByUserId.contains(userId)) {
            logger().info("Skipping for {} is already running.", userId)
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

    fun skipSong(userId: String, track : IPlaylistItem) : Boolean {
        return track.name.contains("live", ignoreCase = true)
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
            if(currentlyPlaying == null) {
                logger().info("currently not playing anything.")
                return 30
            }
            if(!currentlyPlaying.is_playing) {
                logger().info("currently paused.")
                return 10
            }
            val remaining = (currentlyPlaying.item.durationMs - currentlyPlaying.progress_ms) / 1000
            logger().info("Currently playing '{}'. Remaining {}s", currentlyPlaying.item.name, remaining)
            if(skipSong(userId, currentlyPlaying.item)) {
                logger().info("SKIPPED")
                spotifyService.skip(userId)
                return 1
            }
            return if(remaining < 10) { 1 } else { 5 }
        }
    }
}