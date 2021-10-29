package de.adschmidt.songskipper.backend.services

import de.adschmidt.songskipper.backend.api.PlayLogTrack
import de.adschmidt.songskipper.backend.api.Track
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.text.DateFormat
import java.util.*

@Service
class PlayLogService(
    val spotifyService: SpotifyService,
    val ruleService: RuleService,
) {
    suspend fun getRecentTracks(userId: String, limit: Int): List<PlayLogTrack> {
        val historyItems = spotifyService.getRecentTracks(userId, limit)
        val trackIds = historyItems.map { it.track.id }
        val tracksFull = spotifyService.tracks(trackIds.toTypedArray(), userId)
        val tracks = tracksFull.map { Track(it) }
        return tracks.zip(historyItems) { track, historyItem ->
            PlayLogTrack(track, formatDate(historyItem.playedAt), getMatchingRuleIds(userId, track))
        }
    }

    private fun formatDate(date: Date): String {
        val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
            LocaleContextHolder.getLocale()) // TODO cache this by locale
        return formatter.format(date)
    }

    private fun getMatchingRuleIds(userId: String, track: Track): List<String> {
        return ruleService.findMatchingRules(userId, track).mapNotNull { it.id }
    }

}
