package de.adschmidt.songskipper.backend.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUser
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit.*

@Component
class SpotifyApiSupplier(
    private val spotifyUserRepo: SpotifyUserRepo
) : Loggable {

    @Value("\${spring.security.oauth2.client.registration.spotify.client-id}")
    lateinit var clientId : String
    @Value("\${spring.security.oauth2.client.registration.spotify.client-secret}")
    lateinit var clientSecret : String

    /**
     * Return a spotifyApi for the given userId (call this from the background tasks)
     */
    suspend fun buildSpotifyApi(userId : String) : SpotifyApi {
        val user = spotifyUserRepo.findById(userId)
            .orElseThrow{IllegalStateException("User $userId is not known!")} // TODO custom exception types
        refreshTokenIfNeeded(user)
        return SpotifyApi.builder()
            .setAccessToken(user.accessToken)
            .build() // TODO cache based on accessToken
    }

    private suspend fun refreshTokenIfNeeded(user : SpotifyUser) {
        if(user.accessTokenExpiresAt != null &&
            Instant.now().plus(10, MINUTES)
                .isBefore(user.accessTokenExpiresAt)) {
            // token is valid at least another 10 minutes
            return
        }
        if(user.refreshToken == null) {
            throw IllegalStateException("Refresh token is missing for user ${user.id} but the access token expires.")  // TODO custom exception types
        }
        logger().info("Refreshing the access token for {}",user.id)

        val refreshTokenResponse = SpotifyApi.builder().build().authorizationCodeRefresh().refresh_token(user.refreshToken).build()
            .executeAsync().await()
        user.accessToken = refreshTokenResponse.accessToken
        user.accessTokenExpiresAt = Instant.now().plus(refreshTokenResponse.expiresIn.toLong(), SECONDS)
        user.refreshToken = refreshTokenResponse.refreshToken
        spotifyUserRepo.save(user)

        logger().info("The new access token for {} is valid until {}", user.id, user.accessTokenExpiresAt)
    }


}