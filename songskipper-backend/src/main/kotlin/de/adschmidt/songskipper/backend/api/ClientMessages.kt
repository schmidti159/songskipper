package de.adschmidt.songskipper.backend.api

import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified
import se.michaelthelin.spotify.model_objects.specification.Track as SpotifyTrack

data class Artist(
    val name: String,
    val url: String
) {
    constructor(artist: ArtistSimplified) : this(
        artist.name, artist.externalUrls["spotify"]
    )
}

data class Album(
    val title: String,
    val url: String,
    val albumArtUrl: String
) {
    constructor(album: AlbumSimplified) : this(
        album.name, album.externalUrls["spotify"], album.images[0].url,
    )
}

data class Track(
    val title: String,
    val url: String,
    val durationMs: Int,
    val artists: List<Artist>,
    val album: Album
) {
    constructor(track: SpotifyTrack) : this(
        track.name, track.externalUrls["spotify"], track.durationMs,
        track.artists.map { Artist(it) },
        Album(track.album)
    )
}

data class PlayLogTrack(
    val track: Track,
    val playedOn: String,
    val matchingRuleIds: List<String>
)

data class CurrentlyPlayingState(
    val track: Track?,
    val progressMs: Int,
    val isPaused: Boolean
)

data class Rule(
    val id: String? = null,
    val title: String? = null,
    val titleExpression: String? = null,
    val artistExpression: String? = null,
    val albumExpression: String? = null
)

enum class ExpressionType(val marker: String) { REGEX("r"), GLOB("g") }
enum class ExpressionFlag(val marker: String) { WHOLE_WORD("b"), IGNORE_CASE("i") }
data class Expression(
    val type: ExpressionType,
    val pattern: String,
    val flags: List<ExpressionFlag>
) {
    fun hasFlag(flag: ExpressionFlag): Boolean {
        return flags.contains(flag)
    }

    override fun toString(): String {
        return "${type.marker}:$pattern:${flags.map { it.marker }}"
    }

    companion object {
        private val EXPRESSION_REGEX = "^([rg]):(.*):([bi]*)$".toRegex()

        fun parse(string: String?): Expression? {
            if (string == null) {
                return null
            }
            val matchResult = EXPRESSION_REGEX.find(string) ?: return null
            val (typeString, expression, flagsString) = matchResult.destructured
            val type = if (typeString == "r") ExpressionType.REGEX else ExpressionType.GLOB
            val flags = mutableListOf<ExpressionFlag>()
            if (flagsString.contains("b")) {
                flags.add(ExpressionFlag.WHOLE_WORD)
            }
            if (flagsString.contains("i")) {
                flags.add(ExpressionFlag.IGNORE_CASE)
            }
            return Expression(type, expression, flags)
        }
    }

}

