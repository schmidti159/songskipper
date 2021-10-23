package de.adschmidt.songskipper.backend.events

import com.wrapper.spotify.model_objects.specification.AlbumSimplified
import com.wrapper.spotify.model_objects.specification.ArtistSimplified
import com.wrapper.spotify.model_objects.specification.Track as SpotifyTrack

data class Artist (
    val name: String,
    val url: String
) {
    constructor(artist: ArtistSimplified) : this(
        artist.name, artist.externalUrls["spotify"]
    )
}
data class Album (
    val title: String,
    val url: String,
    val albumArtUrl: String
) {
    constructor(album: AlbumSimplified) : this(
        album.name, album.externalUrls["spotify"], album.images[0].url,
    )
}

data class Track (
    val title: String,
    val url: String,
    val durationMs: Int,
    val artists: List<Artist>,
    val album: Album
) {
    constructor(track: SpotifyTrack) : this(
        track.name, track.externalUrls["spotify"], track.durationMs,
        track.artists.map { Artist(it) },
        Album(track.album),

        )
}

data class CurrentlyPlayingState(
    val track: Track?,
    val progressMs: Int,
    val isPaused: Boolean
)

