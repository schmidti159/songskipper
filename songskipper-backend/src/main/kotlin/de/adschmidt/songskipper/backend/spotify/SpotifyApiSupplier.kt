package de.adschmidt.songskipper.backend.spotify

import com.wrapper.spotify.SpotifyApi
import de.adschmidt.songskipper.backend.Loggable
import de.adschmidt.songskipper.backend.logger
import de.adschmidt.songskipper.backend.persistence.model.SpotifyUser
import de.adschmidt.songskipper.backend.persistence.repo.SpotifyUserRepo
import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.lang.Integer.min
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS

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
        if(user.refreshAt != null &&
            Instant.now().isBefore(user.refreshAt)) {
            // token is valid at least another 10 minutes
            return
        }
        if(user.refreshToken == null) {
            throw IllegalStateException("Refresh token is missing for user ${user.id} but the access token expires.")  // TODO custom exception types
        }
        logger().info("Refreshing the access token for {}",user.id)

        val newToken = SpotifyApi.builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(user.refreshToken)
                .build()
                .authorizationCodeRefresh()
                .build()
                .executeAsync().await()
        user.accessToken = newToken.accessToken
        // refresh the token when only half its lifetime is left or if 10 min are left, whichever is smaller
        val refreshTokenWithRemaining = min(newToken.expiresIn/2, 10 * 60)
        user.refreshAt = Instant.now().plus((newToken.expiresIn - refreshTokenWithRemaining).toLong(), SECONDS)
        if(newToken.refreshToken != null) {
            user.refreshToken = newToken.refreshToken
        }
        spotifyUserRepo.save(user)

        logger().info("The new access token for {} will be refreshed at {}", user.id, user.refreshAt)
    }


}