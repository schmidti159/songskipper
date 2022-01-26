package de.adschmidt.songskipper.backend.services

import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying
import se.michaelthelin.spotify.model_objects.specification.PlayHistory
import se.michaelthelin.spotify.model_objects.specification.Track
import de.adschmidt.songskipper.backend.spotify.SpotifyApiSupplier
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Service

@Service
class SpotifyService (
    private val spotifyApiSupplier: SpotifyApiSupplier
) {
    suspend fun getCurrentlyPlayingTrack(userId: String): CurrentlyPlaying? {
        return spotifyApiSupplier.buildSpotifyApi(userId)
            .usersCurrentlyPlayingTrack.build().executeAsync().await()
    }

    suspend fun nextTrack(userId: String) {
        spotifyApiSupplier.buildSpotifyApi(userId)
            .skipUsersPlaybackToNextTrack().build().executeAsync().await()
    }

    suspend fun previousTrack(userId: String) {
        spotifyApiSupplier.buildSpotifyApi(userId)
            .skipUsersPlaybackToPreviousTrack().build().executeAsync().await()
    }

    suspend fun startPlayback(userId: String) {
        spotifyApiSupplier.buildSpotifyApi(userId)
            .startResumeUsersPlayback().build().executeAsync().await()
    }

    suspend fun pausePlayback(userId: String) {
        spotifyApiSupplier.buildSpotifyApi(userId)
            .pauseUsersPlayback().build().executeAsync().await()
    }

    suspend fun track(trackId: String, userId: String): Track? {
        return tracks(arrayOf(trackId), userId)[0]
    }

    // TODO cache this result
    suspend fun tracks(trackIds: Array<String>, userId: String): Array<Track> {
        return spotifyApiSupplier.buildSpotifyApi(userId)
            .getSeveralTracks(*trackIds).build().executeAsync().await()
            ?: emptyArray()
    }

    suspend fun getRecentTracks(userId: String, limit: Int): Array<PlayHistory> {
        return spotifyApiSupplier.buildSpotifyApi(userId)
            .currentUsersRecentlyPlayedTracks.limit(limit).build().executeAsync().await().items
            ?: emptyArray()
    }


}
