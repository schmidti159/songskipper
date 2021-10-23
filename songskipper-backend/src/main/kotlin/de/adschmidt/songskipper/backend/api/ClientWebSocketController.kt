package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.CurrentTrackEvent
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller


@Controller
class ClientWebSocketController(
    val messagingTemplate: SimpMessagingTemplate
): Loggable {

    @EventListener
    fun onApplicationEvent(event: CurrentTrackEvent) {
        messagingTemplate.convertAndSendToUser(event.userId, "/queue/messages", event.currentlyPlayingState)
    }

}