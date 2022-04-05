package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.UserChangedEvent
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import kotlinx.coroutines.*
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@OptIn(DelicateCoroutinesApi::class)
class TrackUpdaterScheduler(
    private val spotifyUserRepo: SpotifyUserRepo,
    private val trackUpdaterService: TrackUpdaterService
) : Loggable {

    private val jobByUserIdMap: MutableMap<String, Job> = mutableMapOf()

    @PostConstruct
    @Scheduled(fixedDelay = 60 * 1000L)
    fun ensureJobsAreRunning() {
        logger().info("making sure all jobs are running.")
        // init threadpool (IO-Threadpool uses at least 64 cores)
        // using GlobalScope, as the coroutine should keep running as a background task the whole time
        spotifyUserRepo.findAll()
            .forEach { it ->
                startJob(it.id)
            }
    }

    @EventListener
    fun userChanged(event: UserChangedEvent) {
        val userId = event.userId
        if (userId == null) {
            logger().error("UserChangedEvent had no user.id")
            return
        }
        startJob(userId)
    }

    private fun startJob(userId: String?) {
        if (userId == null) {
            return // cannot start a job without user id
        }
        // only start the job if the job is currently not running.
        if (jobByUserIdMap[userId] != null && jobByUserIdMap[userId]!!.isActive) {
            return // job is already running
        }
        val job = GlobalScope.launch(Dispatchers.IO) {
            updaterTaskLoop(userId)
        }
        jobByUserIdMap[userId] = job
        logger().info("launched job for user {}", userId)
    }

    private suspend fun updaterTaskLoop(userId: String) {
        while (jobByUserIdMap.containsKey(userId)) {
            val sleepDuration = trackUpdaterService.updateTrack(userId)
            delay(sleepDuration * 1000L)
        }
        logger().info("stopped job for user {}", userId)
    }

}