package de.adschmidt.songskipper.backend.events

import com.wrapper.spotify.model_objects.specification.Track
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


data class CurrentTrackMessage(
    val trackName: String,
    val trackLink: String,
    val artistNames: List<String>,
    val artistLinks: List<String>,
    val albumName: String,
    val albumLink: String,
    val albumArtworkLink: String,
    val durationMs: Int,
    val progressMs: Int
) {

}
