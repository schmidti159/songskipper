package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.api.CurrentlyPlayingState
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.events.CurrentTrackEvent
import de.adschmidt.songskipper.backend.events.SkipEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class TrackUpdaterService(
    private val spotifyService: SpotifyService,
    private val skipperService: SkipperService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : Loggable {

    suspend fun getCurrentlyPlayingState(userId: String): CurrentlyPlayingState {
        val currentlyPlaying = spotifyService.getCurrentlyPlayingTrack(userId)
        if (currentlyPlaying == null || currentlyPlaying.item == null) {
            return CurrentlyPlayingState(null, 0, true)
        }
        val spotifyTrack = spotifyService.track(currentlyPlaying.item.id, userId)
        val apiTrack = if (spotifyTrack != null) Track(spotifyTrack) else null
        return CurrentlyPlayingState(apiTrack, currentlyPlaying.progress_ms, !currentlyPlaying.is_playing)
    }

    /** run the skipper loop and return the seconds to sleep after this run */
    suspend fun updateTrack(userId: String): Long {
        val playingState = getCurrentlyPlayingState(userId)
        applicationEventPublisher.publishEvent(CurrentTrackEvent(this, userId, playingState))
        if (playingState.track == null) {
            return 30
        }
        if (playingState.isPaused) {
            return 10
        }
        if (skipperService.skipTrack(userId, playingState.track)) {
            applicationEventPublisher.publishEvent(SkipEvent(this, userId, playingState.track))
            spotifyService.nextTrack(userId)
            return 1
        }
        val remaining = (playingState.track.durationMs - playingState.progressMs) / 1000
        return if (remaining < 10) {
            1
        } else {
            5
        }
    }
}