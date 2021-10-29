package de.adschmidt.songskipper.backend.services

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying
import com.wrapper.spotify.model_objects.specification.*
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import de.adschmidt.songskipper.backend.spotify.SpotifyApiSupplier
import java.util.*

class SpotifyServiceStub(spotifyUserRepo: SpotifyUserRepo) : SpotifyService(SpotifyApiSupplierStub(spotifyUserRepo)) {

    class SpotifyApiSupplierStub(spotifyUserRepo: SpotifyUserRepo) : SpotifyApiSupplier(spotifyUserRepo) {}

    override suspend fun getCurrentlyPlayingTrack(userId: String): CurrentlyPlaying? {
        return CurrentlyPlaying.Builder()
            .setIs_playing(true)
            .setItem(track("current_track_id"))
            .setProgress_ms(23)
            .build()
    }

    override suspend fun skip(userId: String): String? {
        return null
    }

    override suspend fun tracks(trackIds: Array<String>, userId: String): Array<Track> {
        return trackIds.map { track(it) }.toTypedArray()
    }

    override suspend fun getRecentTracks(userId: String, limit: Int): Array<PlayHistory> {
        return IntRange(0, limit - 1).map {
            PlayHistory.Builder()
                .setPlayedAt(Date(it.toLong()))
                .setTrack(trackSimplified(it.toString()))
                .build()
        }.toTypedArray()
    }

    private fun track(id: String) = Track.Builder()
        .setId(id)
        .setName("name_$id")
        .setAlbum(albumSimplified("album_$id"))
        .setArtists(artistSimplified("artist_$id"))
        .setExternalUrls(externalUrl(id))
        .setDurationMs(42)
        .build()

    private fun trackSimplified(id: String) = TrackSimplified.Builder()
        .setId(id)
        .setName("name_$id")
        .setArtists(artistSimplified("artist_$id"))
        .setDurationMs(42)
        .build()

    private fun artistSimplified(id: String) = ArtistSimplified.Builder()
        .setName("name_$id")
        .setExternalUrls(externalUrl(id))
        .build()

    private fun albumSimplified(id: String) = AlbumSimplified.Builder()
        .setName("name_$id")
        .setExternalUrls(externalUrl(id))
        .setImages(Image.Builder().setUrl("https://image_$id").build())
        .build()

    private fun externalUrl(id: String) = ExternalUrl.Builder()
        .setExternalUrls(mapOf(Pair("spotify", "https://$id")))
        .build()

}