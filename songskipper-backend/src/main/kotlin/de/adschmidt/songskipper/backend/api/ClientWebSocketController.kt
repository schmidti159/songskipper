package de.adschmidt.songskipper.backend.api

import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.events.CurrentTrackEvent
import de.adschmidt.songskipper.backend.events.CurrentTrackMessage
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
        if(event.track == null) {
            return
        }
        val t = event.track
        val msg = CurrentTrackMessage(t.name, t.externalUrls["spotify"],
            t.artists.map{it -> it.name}, t.artists.map{it -> it.externalUrls["spotify"]},
            t.album.name, t.album.externalUrls["spotify"], t.album.images[0].url,
            t.durationMs, event.progressMs)
        messagingTemplate.convertAndSendToUser(event.userId, "/queue/messages", msg)
    }

}