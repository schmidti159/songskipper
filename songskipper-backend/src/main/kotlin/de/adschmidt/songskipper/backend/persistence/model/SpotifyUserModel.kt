package de.adschmidt.songskipper.backend.persistence.model

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "spotify_user")
class SpotifyUserModel(
    @Id
    val id: String? = null,
    var accessToken: String? = null,
    var refreshAt: Instant? = null,
    var refreshToken: String? = null,
    var skipperActive: Boolean = true
) {

}