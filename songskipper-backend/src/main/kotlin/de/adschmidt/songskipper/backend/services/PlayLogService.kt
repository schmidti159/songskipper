package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.events.PlayLogTrack
import de.adschmidt.songskipper.backend.events.Track
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.util.*

@Service
class PlayLogService(
    val spotifyService: SpotifyService,
    val skipperService: SkipperService
) {
    suspend fun getRecentTracks(userId: String, limit: Int): List<PlayLogTrack> {
        val historyItems = spotifyService.getRecentTracks(userId, limit)?.items
            ?: return emptyList()
        val trackIds = historyItems.map {it.track.id}
        val tracksFull = spotifyService.tracks(trackIds.toTypedArray(), userId)
            ?: return emptyList()
        val tracks = tracksFull.map {Track(it)}
        return tracks.zip(historyItems) {track, historyItem ->
            PlayLogTrack(track, formatDate(historyItem.playedAt), getMatchingRuleIds(userId, track))}
    }

    private fun formatDate(date: Date): String {
        val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
            LocaleContextHolder.getLocale()) // TODO cache this by locale
        return formatter.format(date)
    }

    private fun getMatchingRuleIds(userId: String, track: Track): List<String> {
        if(skipperService.skipTrack(userId, track)) {
            return listOf("TODO: editable rules")
        } else {
            return emptyList()
        }
    }

}
