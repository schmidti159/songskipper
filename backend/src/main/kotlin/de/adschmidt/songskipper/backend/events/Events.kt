package de.adschmidt.songskipper.backend.events

import com.wrapper.spotify.model_objects.specification.Track
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


class CurrentTrackEvent(
    source: Any,
    val userId: String,
    val track: Track?,
    val isPlaying: Boolean,
    val progressMs: Int
) : ApplicationEvent(source) {
    override fun toString() : String {
        if(track == null) {
            return "CurrentTrack[nothing]"
        }
        val playing = if(isPlaying) {"playing"} else {"paused"}
        val progress = "(${progressMs/1000}/${track.durationMs/1000})"
        return "[${track.toSimpleString()} $progress, $playing]"
    }
}

fun Track.toSimpleString(): String {
    val artistNames = artists.map{ it.name }
    val artistsString = if(artistNames.size==1) {
        "'${artistNames[0].toString()}'"
    } else {
        artistNames.toString()
    }
    return "'$name' from $artistsString on '${album.name}'"
}

class SkipEvent(
    source: Any,
    val userId: String,
    val track: Track?
) : ApplicationEvent(source) {}

@Component
class EventLogger: Loggable {
    @EventListener
    fun onCurrentTrackEvent(event: CurrentTrackEvent) {
        logger().info("current track: {}", event)
    }

    @EventListener
    fun onSkipEvent(event: SkipEvent) {
        logger().info("skipped: {}", event.track?.toSimpleString())
    }
}