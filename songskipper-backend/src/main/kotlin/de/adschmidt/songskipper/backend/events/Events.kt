package de.adschmidt.songskipper.backend.events

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.api.CurrentlyPlayingState
import de.adschmidt.songskipper.backend.api.Track
import de.adschmidt.songskipper.backend.logger
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


class CurrentTrackEvent(
    source: Any,
    val userId: String,
    val currentlyPlayingState: CurrentlyPlayingState
) : ApplicationEvent(source) {}

class SkipEvent(
    source: Any,
    val userId: String,
    val track: Track?
) : ApplicationEvent(source) {}

class UserChangedEvent(
    source: Any,
    val userId: String?,
    val newUser: Boolean
) : ApplicationEvent(source) {}

@Component
class EventLogger: Loggable {
    @EventListener
    fun onCurrentTrackEvent(event: CurrentTrackEvent) {
        logger().info("current track: {} (user: {})", event.currentlyPlayingState, event.userId)
    }

    @EventListener
    fun onSkipEvent(event: SkipEvent) {
        logger().info("skipped: {} (user: {})", event.track, event.userId)
    }

    @EventListener
    fun onUserChangedEvent(event: UserChangedEvent) {
        logger().info("user data changed: id: {} new: {}", event.userId, event.newUser)
    }
}