package de.adschmidt.songskipper.backend.persistence.model

import java.time.Instant
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "spotify_user")
class SpotifyUserModel(
    /** The id of the user at spotify */
    @Id
    val id: String? = null,
    /** The email addressed of the user how he entered it in spotify */
    var email: String? = null,
    /** The instant of the last login (approximately). Can be used to disable skipping for inactive users.*/
    var lastLogin: Instant? = null,
    /** The current accessToken */
    var accessToken: String? = null,
    /** When the accessToken should be refreshed */
    var refreshAt: Instant? = null,
    /** The refreshToken to refresh the accesstoken. This token valid very long (it probably will never expire).*/
    var refreshToken: String? = null,
    /** Whether the user wants to use the skipper */
    var skipperActive: Boolean = true
) {

}