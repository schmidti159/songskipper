package de.adschmidt.songskipper.backend.services

import com.wrapper.spotify.model_objects.IPlaylistItem
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SpotifySkipperService(
    private val taskScheduler: TaskScheduler,
    private val spotifyService: SpotifyService
) : Loggable {

    fun startSkipping(userId : String) {
        taskScheduler.schedule(SkipperTask(userId), Instant.now())
    }

    fun skipSong(userId: String, track : IPlaylistItem) : Boolean {
        return track.name.contains("live", ignoreCase = true)
    }

    inner class SkipperTask(private val userId: String) : Runnable {

        override fun run() {
            runBlocking { // TODO can this be done non-blocking / with coroutines integrated into the scheduler
                val currentlyPlaying = spotifyService.getCurrentlyPlayingTrack(userId)
                if(currentlyPlaying == null || !currentlyPlaying.is_playing) {
                    logger().info("currently not playing anything or paused.")
                    reschedule(5)
                    return@runBlocking
                }
                val remaining = (currentlyPlaying.item.durationMs - currentlyPlaying.progress_ms) / 1000
                logger().info("Currently playing '{}'. Remaining {}s", currentlyPlaying.item.name, remaining)
                if(skipSong(userId, currentlyPlaying.item)) {
                    logger().info("SKIPPED")
                    spotifyService.skip(userId)
                    reschedule(1)
                } else if(remaining < 30) {
                    reschedule(1)
                } else {
                    reschedule(5)
                }
            }
        }

        private fun reschedule(delay: Long) {
            logger().info("Reschedule in {}s", delay)
            taskScheduler.schedule(this, Instant.now().plusSeconds(delay))
        }

    }

}