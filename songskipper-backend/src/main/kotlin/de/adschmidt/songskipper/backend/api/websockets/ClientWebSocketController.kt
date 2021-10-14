package de.adschmidt.songskipper.backend.api.websockets

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.*
import de.adschmidt.songskipper.backend.logger
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils


@Controller
class ClientWebSocketController(
    val messagingTemplate: SimpMessagingTemplate
): Loggable {

    @EventListener
    fun onApplicationEvent(event: CurrentTrackEvent) {
        val track = if(event.track != null) Track(event.track) else null
        val msg = CurrentlyPlayingMessage(track, event.progressMs, !event.isPlaying)
        messagingTemplate.convertAndSendToUser(event.userId, "/queue/messages", msg)
    }

}