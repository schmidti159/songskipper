package de.adschmidt.songskipper.backend.services

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying
import com.wrapper.spotify.model_objects.specification.PlayHistory
import com.wrapper.spotify.model_objects.specification.Track
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

    suspend fun skip(userId: String): String? {
        return spotifyApiSupplier.buildSpotifyApi(userId)
            .skipUsersPlaybackToNextTrack().build().executeAsync().await()
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
